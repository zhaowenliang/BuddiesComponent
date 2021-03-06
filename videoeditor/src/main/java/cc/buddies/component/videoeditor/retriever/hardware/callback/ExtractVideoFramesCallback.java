package cc.buddies.component.videoeditor.retriever.hardware.callback;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.renderscript.RenderScript;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import cc.buddies.component.videoeditor.VideoEditorUtils;
import cc.buddies.component.videoeditor.retriever.hardware.ExtractVideoFrameConfig;

import java.nio.ByteBuffer;

/**
 * 解析视频帧异步处理回调
 * <br/>帧数据转化为bitmap
 * 参考：https://www.polarxiong.com/archives/Android-YUV_420_888编码Image转换为I420和NV21格式byte数组.html
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExtractVideoFramesCallback extends BaseMediaCodecCallback {

    private static final String TAG = "解析视频帧";

    private MediaExtractor mExtractor;
    private NV21ToBitmap mNv21ToBitmap;
    private ExtractVideoFrameConfig mConfig;

    // 帧间距
    private long frameInterval;

    // 是否已经输出第一帧
    private boolean hasOutputFirstFrame = false;

    private int outputIndex = 0;

    public ExtractVideoFramesCallback(final MediaExtractor extractor,
                                      final RenderScript renderScript,
                                      final ExtractVideoFrameConfig config) {
        this.mExtractor = extractor;
        this.mNv21ToBitmap = new NV21ToBitmap(renderScript);
        this.mConfig = config;
    }

    @Override
    public void onConfig() {
        // 跳到起始时间点
        long startTime = mConfig.startTime;
        this.mExtractor.seekTo(startTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

        // 计算帧间距
        if (frameInterval == 0) {
            mExtractor.advance();
            long time1 = mExtractor.getSampleTime();
            mExtractor.advance();
            long time2 = mExtractor.getSampleTime();

            this.frameInterval = Math.abs(time1 - time2);

            this.mExtractor.seekTo(startTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        }
    }

    // 将读取到的视频帧数据输入到解码器
    @Override
    public void onInputBufferAvailable(@NonNull final MediaCodec codec, final int index) {
        try {
            // 获取不到帧数据，结束
            ByteBuffer inputBuffer = codec.getInputBuffer(index);
            if (inputBuffer == null) {
                codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return;
            }

            // 到达视频尾部，结束
            int sampleData = mExtractor.readSampleData(inputBuffer, 0);
            if (sampleData < 0) {
                codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return;
            }

            // 超出最大设定时间，结束
            long sampleTime = mExtractor.getSampleTime();
            long endTime = mConfig.endTime;
            if (endTime > 0 && sampleTime > endTime) {
                codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return;
            }

            // 顺序处理，当前帧数据放入解码器
            if (mConfig.interval <= 0) {
                codec.queueInputBuffer(index, 0, sampleData, sampleTime, 0);
                mExtractor.advance();
                return;
            }

            // 将帧数据输入解码器
            codec.queueInputBuffer(index, 0, sampleData, sampleTime, 0);
            mExtractor.advance();

        } catch (Exception e) {
            // 中止解码中断线程会报异常
            e.printStackTrace();
        }
    }

    // 解码器解析后视频帧数据
    @Override
    public void onOutputBufferAvailable(@NonNull final MediaCodec codec, final int index, @NonNull final MediaCodec.BufferInfo info) {
        try {
            // 结束
            if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                codec.releaseOutputBuffer(index, false);
                if (onExtractFrameFinishListener != null) {
                    onExtractFrameFinishListener.onExtractFrameFinish(false);
                }
                return;
            }

            if ((1 + outputIndex) > mConfig.maxCount) {
                return;
            }

            boolean isCanOutput = true;

            // 如果当前帧与他前面最近的一个期望时间点 大于一个帧间距 则不是期望结果
            long distance = info.presentationTimeUs % mConfig.interval;
            if (distance > frameInterval * 0.5) {
                isCanOutput = false;
            }

            if (!hasOutputFirstFrame) {
                hasOutputFirstFrame = true;
                isCanOutput = true;
            }

            if (!isCanOutput) {
                codec.releaseOutputBuffer(index, false);
                return;
            }

            outputIndex++;

            // 转换帧格式(Image为原始为压缩图片数据，默认格式为YUV_420_888) -> bitmap
            Image image = codec.getOutputImage(index);
            if (image != null) {
                // YUV_420_888 转换为 NV21
                byte[] bytesImageNv21 = YUVImageUtils.getDataFromImage(image, YUVImageUtils.COLOR_FormatNV21);

                // NV21 转换为 Bitmap
                Rect cropRect = image.getCropRect();
                Bitmap bitmap = mNv21ToBitmap.nv21ToBitmap(bytesImageNv21, cropRect.width(), cropRect.height());
                image.close();

                Bitmap resultBitmap;
                int srcWidth = bitmap.getWidth();
                int srcHeight = bitmap.getHeight();
                int dstWidth = mConfig.targetWidth;
                int dstHeight = mConfig.targetHeight;
                int rotation = mConfig.rotation;

                // bitmap 缩放旋转
                if (dstWidth > 0 && dstHeight > 0) {
                    Matrix transformationMatrix = VideoEditorUtils.getTransformationMatrix(srcWidth, srcHeight, dstWidth, dstHeight, rotation, false, false, true);
                    resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformationMatrix, true);
                    bitmap.recycle();
                } else {
                    resultBitmap = bitmap;
                }

                if (onExtractFrameResultListener != null) {
                    onExtractFrameResultListener.onExtractFrameResult(mConfig.maxCount, info.presentationTimeUs, resultBitmap);
                }
            }

            codec.releaseOutputBuffer(index, false);
        } catch (Exception e) {
            // 中止解码中断线程会报异常
            e.printStackTrace();
        }
    }

    @Override
    public void onError(@NonNull final MediaCodec codec, @NonNull final MediaCodec.CodecException e) {
        Log.e(TAG, "--> onError(): " + e.getMessage());
    }

    @Override
    public void onOutputFormatChanged(@NonNull final MediaCodec codec, @NonNull final MediaFormat format) {
        Log.i(TAG, "--> onOutputFormatChanged()");
    }

}
