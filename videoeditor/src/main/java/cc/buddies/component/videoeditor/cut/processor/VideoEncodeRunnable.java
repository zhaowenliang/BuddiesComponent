package cc.buddies.component.videoeditor.cut.processor;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import cc.buddies.component.videoeditor.cut.VideoCutAsyncTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 视频裁剪 视频编码码处理
 */
public class VideoEncodeRunnable implements Runnable, IVideoEncodeThread {

    private static final String TAG = "VideoEncodeRunnable";

    private Exception mException;
    private VideoProgressAve mProgressAve;

    private AtomicBoolean mDecodeDone;
    private CountDownLatch mMuxerStartLatch;
    private volatile CountDownLatch mEglContextLatch;
    private volatile Surface mSurface;

    private MediaMuxer mMuxer;
    private MediaCodec mEncoder;

    private int mBitrate;
    private int mFrameRate;
    private int mIFrameInterval;
    private int mResultWidth;
    private int mResultHeight;

    public VideoEncodeRunnable(MediaMuxer muxer, int resultWidth, int resultHeight,
                               int dstBitrate, int dstFrameRate, int iFrameInterval,
                               AtomicBoolean decodeDone, CountDownLatch muxerStartLatch) {
        this.mMuxer = muxer;
        this.mBitrate = dstBitrate;
        this.mFrameRate = dstFrameRate;
        this.mIFrameInterval = iFrameInterval;
        this.mResultWidth = resultWidth;
        this.mResultHeight = resultHeight;
        this.mDecodeDone = decodeDone;
        this.mMuxerStartLatch = muxerStartLatch;
        this.mEglContextLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            doEncode();
        } catch (Exception e) {
            mException = e;
        } finally {
            try {
                if (mEncoder != null) {
                    mEncoder.stop();
                    mEncoder.release();
                }
            } catch (Exception e) {
                mException = mException == null ? e : mException;
            }
        }
    }

    private void doEncode() throws IOException {
        String mimeType = VideoCutAsyncTask.OUTPUT_MIME_TYPE;

        mEncoder = MediaCodec.createEncoderByType(mimeType);

        int maxBitrate = VideoUtils.getMaxSupportBitrate(mEncoder, mimeType);
        if (maxBitrate > 0 && mBitrate > maxBitrate) {
            Log.w(TAG, mBitrate + " bitrate too large, set to:" + maxBitrate);
            mBitrate = maxBitrate;
        }

        MediaFormat outputFormat = MediaFormat.createVideoFormat(mimeType, mResultWidth, mResultHeight);
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate);
        outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mIFrameInterval);

        boolean supportProfileHigh = VideoUtils.trySetProfileAndLevel(mEncoder, mimeType, outputFormat,
                MediaCodecInfo.CodecProfileLevel.AVCProfileHigh,
                MediaCodecInfo.CodecProfileLevel.AVCLevel31
        );
        if (supportProfileHigh) {
            Log.i(TAG, "support ProfileHigh, enable ProfileHigh");
        }

        mEncoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();

        mEncoder.start();
        mEglContextLatch.countDown();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean signalEncodeEnd = false;
        int muxerVideoTrackIndex = -1;

        while (!Thread.currentThread().isInterrupted()) {
            if (mDecodeDone.get() && !signalEncodeEnd) {
                signalEncodeEnd = true;
                mEncoder.signalEndOfInputStream();
            }

            int outputBufferIndex = mEncoder.dequeueOutputBuffer(info, VideoCutAsyncTask.TIMEOUT_USEC);
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.i(TAG, "encode outputBufferIndex = " + outputBufferIndex);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mEncoder.getOutputFormat();
                if (muxerVideoTrackIndex < 0) {
                    muxerVideoTrackIndex = mMuxer.addTrack(newFormat);
                    mMuxer.start();
                    mMuxerStartLatch.countDown();
                }
                Log.i(TAG, "encode newFormat = " + newFormat);
            } else if (outputBufferIndex < 0) {
                Log.e(TAG, "unexpected result from decoder.dequeueOutputBuffer: " + outputBufferIndex);
            } else {
                // 编码数据可用
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM && info.presentationTimeUs < 0) {
                    info.presentationTimeUs = 0;
                }

                if (outputBuffer != null) {
                    mMuxer.writeSampleData(muxerVideoTrackIndex, outputBuffer, info);
                }

                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                notifyProgress(info);

                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    Log.i(TAG, "encoderDone");
                    break;
                }
            }
        }
    }

    private void notifyProgress(MediaCodec.BufferInfo info) {
        if (mProgressAve != null) {
            mProgressAve.setVideoTimeStamp((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) > 0 ? Long.MAX_VALUE : info.presentationTimeUs);
        }
    }

    public Exception getException() {
        return this.mException;
    }

    public void setProgressAve(VideoProgressAve progressAve) {
        this.mProgressAve = progressAve;
    }

    @Override
    public Surface getSurface() {
        return this.mSurface;
    }

    @Override
    public CountDownLatch getEglContextLatch() {
        return this.mEglContextLatch;
    }
}
