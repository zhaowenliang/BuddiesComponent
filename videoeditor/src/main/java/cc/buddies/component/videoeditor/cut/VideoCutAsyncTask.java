package cc.buddies.component.videoeditor.cut;

import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import cc.buddies.component.videoeditor.VideoEditorUtils;
import cc.buddies.component.videoeditor.cut.processor.AudioProcessRunnable;
import cc.buddies.component.videoeditor.cut.processor.AudioUtils;
import cc.buddies.component.videoeditor.cut.processor.VideoDecodeRunnable;
import cc.buddies.component.videoeditor.cut.processor.VideoEncodeRunnable;
import cc.buddies.component.videoeditor.cut.processor.VideoProgressAve;
import cc.buddies.component.videoeditor.cut.processor.VideoProgressListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.buddies.component.videoeditor.clip.VideoClipAsyncTask;

/**
 * 视频裁剪压缩异步任务
 * <p>该任务会将视频解码并重新编码，重新编码是为了将视频压缩。
 * 如果不需要压缩可以使用{@link VideoClipAsyncTask}，该方式会有较快的处理速度。
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoCutAsyncTask extends AsyncTask<VideoCutAsyncTask.Processor, Float, String> {

    private static final String TAG = "VideoCutAsyncTask";

    public final static String OUTPUT_MIME_TYPE = "video/avc";

    public static int DEFAULT_FRAME_RATE = 20;

    public final static int DEFAULT_I_FRAME_INTERVAL = 1;

    public final static int DEFAULT_AAC_BITRATE = 192 * 1000;

    public final static int TIMEOUT_USEC = 2500;

    private Exception mException;
    private OnVideoCutCallback onVideoCutCallback;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private Future<Boolean> mFutureVideoDecode;
    private Future<Boolean> mFutureVideoEncode;
    private Future<Boolean> mFutureAudioProcess;

    public void setOnVideoCutCallback(final OnVideoCutCallback onVideoCutCallback) {
        this.onVideoCutCallback = onVideoCutCallback;
    }

    public VideoCutAsyncTask() {
        this.mThreadPoolExecutor = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
    }

    @Deprecated
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onVideoCutCallback != null) {
            onVideoCutCallback.onStart();
        }
    }

    @Deprecated
    @Override
    protected String doInBackground(final Processor... processors) {
        Processor processor = processors[0];
        processor.progressListener(this::publishProgress);

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(processor.input);
            int originWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int originHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int rotationValue = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            int oriBitrate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            int durationMs = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            retriever.release();

            if (processor.bitrate == null) {
                processor.bitrate = oriBitrate;
            }
            if (processor.iFrameInterval == null) {
                processor.iFrameInterval = DEFAULT_I_FRAME_INTERVAL;
            }

            int resultWidth = processor.outWidth == null ? originWidth : processor.outWidth;
            int resultHeight = processor.outHeight == null ? originHeight : processor.outHeight;
            resultWidth = resultWidth % 2 == 0 ? resultWidth : resultWidth + 1;
            resultHeight = resultHeight % 2 == 0 ? resultHeight : resultHeight + 1;

            if (rotationValue % 180 == 90) {
                int temp = resultHeight;
                //noinspection SuspiciousNameCombination
                resultHeight = resultWidth;
                resultWidth = temp;
            }

            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(processor.input);

            int videoIndex = VideoEditorUtils.getExtractorMediaTrackIndex(extractor, "video/");
            int audioIndex = VideoEditorUtils.getExtractorMediaTrackIndex(extractor, "audio/");

            MediaMuxer mediaMuxer = new MediaMuxer(processor.output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            if (processor.degrees != null) {
                mediaMuxer.setOrientationHint(processor.degrees % 360);
            }

            Integer audioEndTimeMs = processor.endTimeMs;
            int muxerAudioTrackIndex = 0;

            if (audioIndex >= 0) {
                MediaFormat audioTrackFormat = extractor.getTrackFormat(audioIndex);

                int bitrate = AudioUtils.getAudioBitrate(audioTrackFormat);
                int channelCount = audioTrackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                int sampleRate = audioTrackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                int maxBufferSize = AudioUtils.getAudioMaxBufferSize(audioTrackFormat);

                // 参数对应-> mime、采样率、声道数
                MediaFormat audioEncodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
                audioEncodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
                audioEncodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                audioEncodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize);

                long videoDurationUs = durationMs * 1000;
                long audioDurationUs = audioTrackFormat.getLong(MediaFormat.KEY_DURATION);

                if (processor.startTimeMs != null || processor.endTimeMs != null) {
                    if (processor.startTimeMs != null && processor.endTimeMs != null) {
                        videoDurationUs = (processor.endTimeMs - processor.startTimeMs) * 1000;
                    }

                    long avDurationUs = Math.min(videoDurationUs, audioDurationUs);
                    audioEncodeFormat.setLong(MediaFormat.KEY_DURATION, avDurationUs);

                    audioEndTimeMs = (processor.startTimeMs == null ? 0 : processor.startTimeMs) + (int) (avDurationUs / 1000);
                }

                AudioUtils.checkCsd(audioEncodeFormat, MediaCodecInfo.CodecProfileLevel.AACObjectLC, sampleRate, channelCount);

                // 提前推断出音頻格式加到MediaMuxer，不然实际上应该到音频预处理完才能addTrack，会卡住视频编码的进度
                muxerAudioTrackIndex = mediaMuxer.addTrack(audioEncodeFormat);
            }

            // 选择视频轨道
            extractor.selectTrack(videoIndex);

            // 跳到开始操作时间点
            long seekStartTime = processor.startTimeMs == null ? 0 : processor.startTimeMs * 1000;
            extractor.seekTo(seekStartTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

            // 进度计算及回调进度工具
            VideoProgressAve progressAve = new VideoProgressAve(processor.listener);
            progressAve.setStartTimeMs(processor.startTimeMs == null ? 0 : processor.startTimeMs);
            progressAve.setEndTimeMs(processor.endTimeMs == null ? durationMs : processor.endTimeMs);

            // 线程同步锁
            AtomicBoolean decodeDone = new AtomicBoolean(false);
            CountDownLatch muxerStartLatch = new CountDownLatch(1);

            MediaFormat videoTrackFormat = extractor.getTrackFormat(videoIndex);
            int srcFrameRate = videoTrackFormat.containsKey(MediaFormat.KEY_FRAME_RATE) ? videoTrackFormat.getInteger(MediaFormat.KEY_FRAME_RATE) : -1;
            int dstFrameRate = processor.frameRate == null ? DEFAULT_FRAME_RATE : processor.frameRate;
            dstFrameRate = dstFrameRate > 0 ? dstFrameRate : srcFrameRate;

            VideoEncodeRunnable encodeThread = new VideoEncodeRunnable(mediaMuxer, resultWidth, resultHeight,
                    processor.bitrate, dstFrameRate, processor.iFrameInterval, decodeDone, muxerStartLatch);

            VideoDecodeRunnable decodeThread = new VideoDecodeRunnable(encodeThread, extractor, processor.startTimeMs, processor.endTimeMs,
                    srcFrameRate, dstFrameRate, processor.dropFrames, videoIndex, decodeDone);

            AudioProcessRunnable audioProcessThread = new AudioProcessRunnable(mediaMuxer, processor.input,
                    processor.startTimeMs, audioEndTimeMs, muxerAudioTrackIndex, muxerStartLatch);

            encodeThread.setProgressAve(progressAve);
            audioProcessThread.setProgressAve(progressAve);

            long beginTaskTime = System.currentTimeMillis();

            // 三个线程放入线程池
            mFutureVideoDecode = mThreadPoolExecutor.submit(decodeThread, true);
            mFutureVideoEncode = mThreadPoolExecutor.submit(encodeThread, true);
            mFutureAudioProcess = mThreadPoolExecutor.submit(audioProcessThread, true);

            Log.w(TAG, "视频解码完成: " + mFutureVideoDecode.get());
            Log.w(TAG, "视频编码完成: " + mFutureVideoEncode.get());
            Log.w(TAG, "音频处理完成: " + mFutureAudioProcess.get());
            Log.w(TAG, "视频裁剪压缩总时间: " + (System.currentTimeMillis() - beginTaskTime));

            mediaMuxer.release();
            extractor.release();

            if (encodeThread.getException() != null) {
                throw encodeThread.getException();
            } else if (decodeThread.getException() != null) {
                throw decodeThread.getException();
            } else if (audioProcessThread.getException() != null) {
                throw audioProcessThread.getException();
            }

            return processor.getOutput();
        } catch (Exception e) {
            e.printStackTrace();
            this.mException = e;
            return null;
        }
    }

    @Deprecated
    @Override
    protected void onProgressUpdate(final Float... values) {
        super.onProgressUpdate(values);
        if (onVideoCutCallback != null) {
            onVideoCutCallback.onProgress(values[0]);
        }
    }

    @Deprecated
    @Override
    protected void onPostExecute(final String path) {
        super.onPostExecute(path);
        cancelFutures();

        if (!TextUtils.isEmpty(path)) {
            if (onVideoCutCallback != null) onVideoCutCallback.onSuccess(path);
        } else {
            if (onVideoCutCallback != null) onVideoCutCallback.onError(this.mException);
        }
    }

    @Override
    protected void onCancelled(final String s) {
        super.onCancelled(s);
        cancelFutures();

        if (onVideoCutCallback != null) {
            onVideoCutCallback.onCancel(s);
        }
    }

    // 取消所有未完成任务
    private void cancelFutures() {
        if (mFutureVideoDecode != null && !mFutureVideoDecode.isDone()) {
            mFutureVideoDecode.cancel(true);
            mFutureVideoDecode = null;
        }
        if (mFutureVideoEncode != null && !mFutureVideoEncode.isDone()) {
            mFutureVideoEncode.cancel(true);
            mFutureVideoEncode = null;
        }
        if (mFutureAudioProcess != null && !mFutureAudioProcess.isDone()) {
            mFutureAudioProcess.cancel(true);
            mFutureAudioProcess = null;
        }
    }

    public interface OnVideoCutCallback {
        void onStart();

        void onProgress(final float progress);

        void onSuccess(@NonNull final String output);

        void onError(@NonNull final Exception e);

        void onCancel(@NonNull final String output);
    }

    public static class Processor {
        private String input;
        private String output;

        @Nullable
        private Integer outWidth;
        @Nullable
        private Integer outHeight;
        @Nullable
        private Integer degrees;
        @Nullable
        private Integer startTimeMs;
        @Nullable
        private Integer endTimeMs;
        @Nullable
        private Integer bitrate;
        @Nullable
        private Integer frameRate;
        @Nullable
        private Integer iFrameInterval;
        @Nullable
        private VideoProgressListener listener;

        /**
         * 帧率超过指定帧率时是否丢帧
         */
        private boolean dropFrames = true;

        public Processor input(String input) {
            this.input = input;
            return this;
        }

        public Processor output(String output) {
            this.output = output;
            return this;
        }

        public Processor outWidth(int outWidth) {
            this.outWidth = outWidth;
            return this;
        }

        public Processor outHeight(int outHeight) {
            this.outHeight = outHeight;
            return this;
        }

        public Processor degrees(int degrees) {
            this.degrees = degrees;
            return this;
        }

        public Processor startTimeMs(int startTimeMs) {
            this.startTimeMs = startTimeMs;
            return this;
        }

        public Processor endTimeMs(int endTimeMs) {
            this.endTimeMs = endTimeMs;
            return this;
        }

        public Processor bitrate(int bitrate) {
            this.bitrate = bitrate;
            return this;
        }

        public Processor frameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public Processor iFrameInterval(int iFrameInterval) {
            this.iFrameInterval = iFrameInterval;
            return this;
        }

        /**
         * 帧率超过指定帧率时是否丢帧，默认为true
         */
        public Processor dropFrames(boolean dropFrames) {
            this.dropFrames = dropFrames;
            return this;
        }

        public Processor progressListener(VideoProgressListener listener) {
            this.listener = listener;
            return this;
        }

        public String getOutput() {
            return this.output;
        }
    }

}
