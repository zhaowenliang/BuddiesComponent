package cc.buddies.component.videoeditor.clip;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import cc.buddies.component.videoeditor.Utils;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * 视频裁剪异步任务
 */
public class VideoClipAsyncTask extends AsyncTask<Long, Long, String> {

    private static final String TAG = "VideoClipAsyncTask";

    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;

    private String url;             // 视频地址
    private String outPath;         // 输出目录
    private int rotate;             // 旋转角度

    private int width;              // 视频宽
    private int height;             // 视频高
    private int orientation;        // 视频旋转角度
    private long duration;          // 视频时长

    private long curProgress;       // 任务处理当前进度
    private long totalProgress;     // 任务处理音视频总进度 视频时间+音频时间

    // 处理回调
    private OnClipCallBack onClipCallBack;

    public VideoClipAsyncTask(final String url, final String outPath) {
        this.url = url;
        this.outPath = outPath;
    }

    /**
     * 设置视频旋转角度
     *
     * @param rotate 旋转角度 90的倍数
     */
    public void setRotate(final int rotate) {
        this.rotate = rotate;
    }

    /**
     * 设置视频裁剪处理回调
     *
     * @param onClipCallBack 回调
     */
    public void setOnClipCallBack(final OnClipCallBack onClipCallBack) {
        this.onClipCallBack = onClipCallBack;
    }

    @MainThread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onClipCallBack != null) {
            if (TextUtils.isEmpty(url)) {
                onClipCallBack.onError(null, new Exception("视频路径为空"));
                cancel(true);
            } else if (TextUtils.isEmpty(outPath)) {
                onClipCallBack.onError(null, new Exception("输出路径为空"));
                cancel(true);
            } else {
                onClipCallBack.onStart();
            }
        }
    }

    @WorkerThread
    @Override
    protected String doInBackground(final Long... longs) {
        long clipPoint = longs[0];
        long clipDuration = longs[1];

        String fileName = "VIDEO_" + System.currentTimeMillis() + ".mp4";
        File file = new File(outPath, fileName);
        if (file.exists()) {
            boolean delete = file.delete();
            Log.d(TAG, "剪辑视频，删除输出路径同名文件:" + delete);
        }

        String filePath = file.getAbsolutePath();

        try {
            // 裁剪视频
            clipVideo(url, filePath, clipPoint, clipDuration);
            return filePath;
        } catch (Exception e) {
            if (onClipCallBack != null) {
                onClipCallBack.onError(filePath, e);
            }
            return null;
        }
    }

    @MainThread
    @Override
    protected void onProgressUpdate(final Long... values) {
        super.onProgressUpdate(values);
        if (onClipCallBack != null && values.length > 1) {
            onClipCallBack.onProcess(values[0], values[1]);
        }
    }

    @MainThread
    @Override
    protected void onPostExecute(final String outFilePath) {
        super.onPostExecute(outFilePath);
        // 这里需要先结束视频处理再回调完成，否则此时用MediaMetadataRetriever获取视频帧会获取不到。
        _release();

        if (onClipCallBack != null && !TextUtils.isEmpty(outFilePath)) {
            onClipCallBack.onComplete(outFilePath, width, height, orientation, duration);
        }
    }

    @MainThread
    @Override
    protected void onCancelled(final String s) {
        super.onCancelled(s);
        _release();

        if (onClipCallBack != null) {
            onClipCallBack.onCancelled(s);
        }
    }

    /**
     * 裁剪视频
     *
     * @param url          视频地址
     * @param outFileUrl   输出文件地址
     * @param clipPoint    裁剪开始时间点
     * @param clipDuration 裁剪结束时间点
     * @throws Exception 处理异常
     */
    private void clipVideo(String url, String outFileUrl, long clipPoint, long clipDuration) throws Exception {
        // 创建分离器
        this.mMediaExtractor = new MediaExtractor();
        // 设置文件路径
        this.mMediaExtractor.setDataSource(url);
        // 创建合成器
        this.mMediaMuxer = new MediaMuxer(outFileUrl, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        long clipVideoDuration = 0, clipAudioDuration = 0;
        int videoMaxInputSize = 0, audioMaxInputSize = 0;
        int muxerVideoTrackIndex = -1, muxerAudioTrackIndex = -1;

        // 获取视频轨道
        int videoTrackIndex = Utils.getExtractorMediaTrackIndex(this.mMediaExtractor, "video/");
        if (videoTrackIndex >= 0) {
            MediaFormat videoFormat = this.mMediaExtractor.getTrackFormat(videoTrackIndex);

            int videoWidth = videoFormat.getInteger(MediaFormat.KEY_WIDTH);
            int videoHeight = videoFormat.getInteger(MediaFormat.KEY_HEIGHT);
            long videoDuration = videoFormat.getLong(MediaFormat.KEY_DURATION);
            int videoRotation = Utils.getVideoRotation(videoFormat, url);
            videoMaxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

            // 配置宽高为偶数
            this.width = videoWidth % 2 == 0 ? videoWidth : videoWidth + 1;
            this.height = videoHeight % 2 == 0 ? videoHeight : videoHeight + 1;

            // 校正裁剪视频时长
            clipVideoDuration = (videoDuration - clipPoint) < clipDuration ? (videoDuration - clipPoint) : clipDuration;

            videoFormat.setLong(MediaFormat.KEY_DURATION, clipVideoDuration);
            videoFormat.setInteger(MediaFormat.KEY_WIDTH, width);
            videoFormat.setInteger(MediaFormat.KEY_HEIGHT, height);

            // 如果有视频轨道，则设置旋转角度   The supported angles are 0, 90, 180, and 270 degrees.
            // MediaMuxer中放入音视频轨道后如果不放入视频旋转角度，会丢失视频角度信息。
            orientation = videoRotation + this.rotate;
            mMediaMuxer.setOrientationHint(orientation % 360);

            // 向合成器添加视频轨
            muxerVideoTrackIndex = mMediaMuxer.addTrack(videoFormat);
        }

        // 获取音频轨道
        int audioTrackIndex = Utils.getExtractorMediaTrackIndex(mMediaExtractor, "audio/");
        if (audioTrackIndex >= 0) {
            MediaFormat audioFormat = mMediaExtractor.getTrackFormat(audioTrackIndex);

            // int audioSampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            // int audioChannelCount = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            long audioDuration = audioFormat.getLong(MediaFormat.KEY_DURATION);
            audioMaxInputSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

            // 校正裁剪音频时长
            clipAudioDuration = (audioDuration - clipPoint) < clipDuration ? (audioDuration - clipPoint) : clipDuration;

            audioFormat.setLong(MediaFormat.KEY_DURATION, clipAudioDuration);

            // 添加音轨
            muxerAudioTrackIndex = mMediaMuxer.addTrack(audioFormat);
        }

        // 视频时长记录
        this.duration = clipVideoDuration != 0 ? clipVideoDuration : clipAudioDuration;

        // 检测剪辑点和剪辑时长是否正确
        if (clipPoint >= this.duration) {
            throw new Exception("clip point is error!");
        }

        // 初始化进度
        this.curProgress = 0;
        this.totalProgress = clipVideoDuration + clipAudioDuration;
        publishProgress(this.curProgress, this.totalProgress);

        // 根据官方文档的解释MediaMuxer的start一定要在addTrack之后
        mMediaMuxer.start();

        // 处理视频轨道
        long videoBeginTime = 0;
        if (muxerVideoTrackIndex != -1) {
            ByteBuffer videoInputBuffer = ByteBuffer.allocate(videoMaxInputSize);
            videoBeginTime = dealVideo(videoInputBuffer, videoTrackIndex, muxerVideoTrackIndex, clipPoint, clipVideoDuration);
        }

        // 处理音频轨道
        if (muxerAudioTrackIndex != -1) {
            ByteBuffer audioInputBuffer = ByteBuffer.allocate(audioMaxInputSize);
            long audioBeginTime = videoBeginTime <= 0 ? clipPoint : videoBeginTime;
            dealAudio(audioInputBuffer, audioTrackIndex, muxerAudioTrackIndex, audioBeginTime, clipAudioDuration);
        }
    }

    // 视频处理部分
    private long dealVideo(ByteBuffer inputBuffer, int trackIndex, int muxerTrackIndex, long startTimeUs, long clipDuration) {
        mMediaExtractor.selectTrack(trackIndex);
        MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();

        mMediaExtractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        long beginTime = -1;
        long endTimeUs = startTimeUs + clipDuration;

        while (true) {
            long sampleTime = mMediaExtractor.getSampleTime();
            int sampleFlag = mMediaExtractor.getSampleFlags();
            if (sampleTime == -1 || sampleTime > endTimeUs) {
                mMediaExtractor.unselectTrack(trackIndex);
                break;
            }

            if (beginTime == -1 && sampleFlag != MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                continue;
            }

            if (beginTime == -1) {
                beginTime = sampleTime;
                endTimeUs = beginTime + clipDuration;
            }

            videoInfo.offset = 0;
            videoInfo.flags = sampleFlag;
            videoInfo.size = mMediaExtractor.readSampleData(inputBuffer, 0);
            videoInfo.presentationTimeUs = sampleTime - beginTime;

            if (videoInfo.size <= 0) {
                break;
            }

            mMediaMuxer.writeSampleData(muxerTrackIndex, inputBuffer, videoInfo);
            mMediaExtractor.advance();

            if (onClipCallBack != null) {
                this.curProgress = videoInfo.presentationTimeUs;
                publishProgress(curProgress, totalProgress);
            }
        }

        return beginTime;
    }

    // 处理音频部分
    private void dealAudio(ByteBuffer inputBuffer, int trackIndex, int muxerTrackIndex, long startTimeUs, long clipDuration) {
        mMediaExtractor.selectTrack(trackIndex);
        MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();

        // 选择起点
        mMediaExtractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        long beginTime = -1;
        long endTimeUs = startTimeUs + clipDuration;
        long myProgress = this.curProgress;

        while (true) {
            long sampleTime = mMediaExtractor.getSampleTime();
            if (sampleTime == -1 || sampleTime > endTimeUs) {
                mMediaExtractor.unselectTrack(trackIndex);
                break;
            }

            if (sampleTime < startTimeUs) {
                mMediaExtractor.advance();
                continue;
            }

            if (beginTime == -1) {
                beginTime = sampleTime;
                endTimeUs = beginTime + clipDuration;
            }

            audioInfo.offset = 0;
            audioInfo.flags = mMediaExtractor.getSampleFlags();
            audioInfo.size = mMediaExtractor.readSampleData(inputBuffer, 0);
            audioInfo.presentationTimeUs = sampleTime - beginTime;

            if (audioInfo.size <= 0) {
                break;
            }

            mMediaMuxer.writeSampleData(muxerTrackIndex, inputBuffer, audioInfo);
            mMediaExtractor.advance();

            if (onClipCallBack != null) {
                this.curProgress = myProgress + audioInfo.presentationTimeUs;
                publishProgress(curProgress, totalProgress);
            }
        }
    }

    /**
     * 释放资源
     */
    private void _release() {
        if (mMediaMuxer != null) {
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
        if (mMediaExtractor != null) {
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
    }

    /**
     * 处理回调
     */
    public interface OnClipCallBack {
        void onStart();

        void onProcess(long curTime, long totalTime);

        void onComplete(@NonNull String outFile, int width, int height, int orientation, long duration);

        void onCancelled(@NonNull String outFile);

        void onError(@Nullable String outFile, @NonNull Exception e);
    }

}
