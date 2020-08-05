package cc.buddies.component.videoeditor.cut.processor;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

import cc.buddies.component.videoeditor.cut.VideoCutAsyncTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 视频裁剪 视频解码处理
 */
public class VideoDecodeRunnable implements Runnable {

    private static final String TAG = "VideoDecodeRunnable";

    private Exception mException;
    private AtomicBoolean mDecodeDone;
    private IVideoEncodeThread mVideoEncodeThread;

    private MediaExtractor mExtractor;
    private MediaCodec mDecoder;
    private int mVideoIndex;

    private InputSurface mInputSurface;
    private OutputSurface mOutputSurface;

    private Integer mStartTimeMs;
    private Integer mEndTimeMs;
    private Integer mDstFrameRate;
    private Integer mSrcFrameRate;
    private boolean mDropFrames;
    private FrameDropper mFrameDropper;

    public VideoDecodeRunnable(IVideoEncodeThread videoEncodeThread, MediaExtractor extractor,
                               @Nullable Integer startTimeMs, @Nullable Integer endTimeMs,
                               @Nullable Integer srcFrameRate, @Nullable Integer dstFrameRate,
                               boolean dropFrames, int videoIndex, AtomicBoolean decodeDone) {
        mVideoEncodeThread = videoEncodeThread;
        mExtractor = extractor;
        mStartTimeMs = startTimeMs;
        mEndTimeMs = endTimeMs;
        mVideoIndex = videoIndex;
        mDecodeDone = decodeDone;
        mSrcFrameRate = srcFrameRate;
        mDstFrameRate = dstFrameRate;
        mDropFrames = dropFrames;
    }

    @Override
    public void run() {
        try {
            doDecode();
        } catch (Exception e) {
            mException = e;
        } finally {
            if (mInputSurface != null) {
                mInputSurface.release();
            }
            if (mOutputSurface != null) {
                mOutputSurface.release();
            }
            try {
                if (mDecoder != null) {
                    mDecoder.stop();
                    mDecoder.release();
                }
            } catch (Exception e) {
                mException = mException == null ? e : mException;
            }
        }
    }

    private void doDecode() throws IOException {
        CountDownLatch eglContextLatch = mVideoEncodeThread.getEglContextLatch();
        try {
            boolean await = eglContextLatch.await(5, TimeUnit.SECONDS);
            if (!await) {
                mException = new TimeoutException("wait eglContext timeout!");
                return;
            }
        } catch (InterruptedException e) {
            mException = e;
            return;
        }

        Surface encodeSurface = mVideoEncodeThread.getSurface();
        mInputSurface = new InputSurface(encodeSurface);
        mInputSurface.makeCurrent();

        // 初始化解码器
        MediaFormat inputFormat = mExtractor.getTrackFormat(mVideoIndex);
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String decoderName = mediaCodecList.findDecoderForFormat(inputFormat);
        mDecoder = MediaCodec.createByCodecName(decoderName);
        mOutputSurface = new OutputSurface();
        mDecoder.configure(inputFormat, mOutputSurface.getSurface(), null, 0);
        mDecoder.start();

        // 丢帧判断
        int frameIndex = 0;
        if (mDropFrames && mSrcFrameRate != null && mDstFrameRate != null) {
            if (mSrcFrameRate > mDstFrameRate) {
                mFrameDropper = new FrameDropper(mSrcFrameRate, mDstFrameRate);
                Log.w(TAG, "帧率过高，需要丢帧:" + mSrcFrameRate + " -> " + mDstFrameRate);
            }
        }

        // 开始解码
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean decoderDone = false;
        boolean inputDone = false;
        long videoStartTimeUs = -1;

        while (!Thread.currentThread().isInterrupted() && !decoderDone) {
            if (!inputDone) {
                boolean eof = false;
                int index = mExtractor.getSampleTrackIndex();
                if (index == mVideoIndex) {
                    int inputBufIndex = mDecoder.dequeueInputBuffer(VideoCutAsyncTask.TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        ByteBuffer inputBuf = mDecoder.getInputBuffer(inputBufIndex);
                        if (inputBuf != null) {
                            int chunkSize = mExtractor.readSampleData(inputBuf, 0);
                            if (chunkSize < 0) {
                                mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                decoderDone = true;
                                Log.i(TAG, "decoderDone");
                            } else {
                                long sampleTime = mExtractor.getSampleTime();
                                mDecoder.queueInputBuffer(inputBufIndex, 0, chunkSize, sampleTime, 0);
                                mExtractor.advance();
                            }
                        }
                    }
                } else if (index == -1) {
                    eof = true;
                }

                if (eof) {
                    // 解码输入结束
                    Log.i(TAG, "inputDone");
                    int inputBufIndex = mDecoder.dequeueInputBuffer(VideoCutAsyncTask.TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }

            while (!Thread.currentThread().isInterrupted() && !decoderDone) {
                int outputBufferIndex = mDecoder.dequeueOutputBuffer(info, VideoCutAsyncTask.TIMEOUT_USEC);
                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.i(TAG, "decode outputBufferIndex = " + outputBufferIndex);
                    break;
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    Log.i(TAG, "decode newFormat = " + mDecoder.getOutputFormat());
                } else if (outputBufferIndex < 0) {
                    Log.e(TAG, "unexpected result from decoder.dequeueOutputBuffer: " + outputBufferIndex);
                } else {
                    boolean doRender = true;
                    // 解码数据可用
                    if (mEndTimeMs != null && info.presentationTimeUs >= mEndTimeMs * 1000) {
                        inputDone = true;
                        decoderDone = true;
                        doRender = false;
                        info.flags |= MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                    }

                    if (mStartTimeMs != null && info.presentationTimeUs < mStartTimeMs * 1000) {
                        doRender = false;
                        Log.e(TAG, "drop frame startTime = " + mStartTimeMs + " present time = " + info.presentationTimeUs / 1000);
                    }

                    if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        decoderDone = true;
                        mDecoder.releaseOutputBuffer(outputBufferIndex, false);
                        Log.i(TAG, "decoderDone");
                        break;
                    }

                    // 检查是否需要丢帧
                    if (mFrameDropper != null && mFrameDropper.checkDrop(frameIndex)) {
                        Log.w(TAG, "帧率过高，丢帧: " + frameIndex);
                        doRender = false;
                    }

                    frameIndex++;
                    mDecoder.releaseOutputBuffer(outputBufferIndex, doRender);

                    if (doRender) {
                        boolean errorWait = false;
                        try {
                            mOutputSurface.awaitNewImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorWait = true;
                        }
                        if (!errorWait) {
                            if (videoStartTimeUs == -1) {
                                videoStartTimeUs = info.presentationTimeUs;
                                Log.i(TAG, "videoStartTime: " + videoStartTimeUs / 1000);
                            }
                            mOutputSurface.drawImage(false);
                            long presentationTimeNs = (info.presentationTimeUs - videoStartTimeUs) * 1000;
                            Log.i(TAG, "drawImage, setPresentationTimeMs: " + presentationTimeNs / 1000 / 1000);
                            mInputSurface.setPresentationTime(presentationTimeNs);
                            mInputSurface.swapBuffers();
                            break;
                        }
                    }
                }
            }
        }

        mDecodeDone.set(true);
    }

    public Exception getException() {
        return mException;
    }

}