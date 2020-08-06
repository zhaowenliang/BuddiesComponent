package cc.buddies.component.videoeditor.cut.processor;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import androidx.annotation.Nullable;

import cc.buddies.component.videoeditor.VideoEditorUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 视频裁剪 音频处理
 */
public class AudioProcessRunnable implements Runnable, VideoProgressListener {

    private static final String TAG = "AudioProcessRunnable";

    private Exception mException;
    private VideoProgressAve mProgressAve;

    private String mVideoPath;
    private Integer mStartTimeMs;
    private Integer mEndTimeMs;

    private MediaMuxer mMuxer;
    private int mMuxerAudioTrackIndex;
    private CountDownLatch mMuxerStartLatch;

    private MediaExtractor mExtractor;

    public AudioProcessRunnable(MediaMuxer muxer, String videoPath,
                                @Nullable Integer startTimeMs, @Nullable Integer endTimeMs,
                                int muxerAudioTrackIndex, CountDownLatch muxerStartLatch) {
        this.mMuxer = muxer;
        this.mVideoPath = videoPath;
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
        this.mMuxerAudioTrackIndex = muxerAudioTrackIndex;
        this.mMuxerStartLatch = muxerStartLatch;

        this.mExtractor = new MediaExtractor();
    }

    @Override
    public void run() {
        try {
            doProcessAudio();
        } catch (Exception e) {
            this.mException = e;
        } finally {
            this.mExtractor.release();
        }
    }

    private void doProcessAudio() throws Exception {
        this.mExtractor.setDataSource(mVideoPath);

        int audioTrackIndex = VideoEditorUtils.getExtractorMediaTrackIndex(mExtractor, "audio/");
        if (audioTrackIndex >= 0) {
            mExtractor.selectTrack(audioTrackIndex);

            Integer startTimeUs = mStartTimeMs == null ? null : mStartTimeMs * 1000;
            Integer endTimeUs = mEndTimeMs == null ? null : mEndTimeMs * 1000;

            boolean await = mMuxerStartLatch.await(3, TimeUnit.SECONDS);
            if (!await) {
                throw new TimeoutException("wait muxerStartLatch timeout!");
            }

            startTimeUs = startTimeUs == null ? 0 : startTimeUs;
            mExtractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

            MediaFormat audioFormat = mExtractor.getTrackFormat(audioTrackIndex);
            long durationUs = audioFormat.getLong(MediaFormat.KEY_DURATION);
            int maxBufferSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

            ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

            while (!Thread.currentThread().isInterrupted()) {
                long sampleTimeUs = mExtractor.getSampleTime();
                if (sampleTimeUs == -1 || (endTimeUs != null && sampleTimeUs > endTimeUs)) {
                    break;
                }

                if (sampleTimeUs < startTimeUs) {
                    mExtractor.advance();
                    continue;
                }

                float progress = (sampleTimeUs - startTimeUs) / (float) (endTimeUs == null ? durationUs : endTimeUs - startTimeUs);
                progress = progress < 0 ? 0 : progress;
                progress = progress > 1 ? 1 : progress;
                onProgress(progress);

                info.presentationTimeUs = sampleTimeUs - startTimeUs;
                info.flags = mExtractor.getSampleFlags();
                info.size = mExtractor.readSampleData(buffer, 0);
                if (info.size < 0) {
                    break;
                }

                Log.d(TAG, "writeAudioSampleData, time:" + info.presentationTimeUs / 1000F);
                mMuxer.writeSampleData(mMuxerAudioTrackIndex, buffer, info);
                mExtractor.advance();
            }
        }

        if (mProgressAve != null) {
            mProgressAve.setAudioProgress(1);
        }
    }

    public Exception getException() {
        return mException;
    }

    public void setProgressAve(VideoProgressAve progressAve) {
        mProgressAve = progressAve;
    }

    @Override
    public void onProgress(final float progress) {
        if (mProgressAve != null) {
            mProgressAve.setAudioProgress(progress);
        }
    }
}
