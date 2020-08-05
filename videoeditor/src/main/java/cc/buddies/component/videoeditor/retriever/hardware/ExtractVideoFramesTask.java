package cc.buddies.component.videoeditor.retriever.hardware;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import cc.buddies.component.videoeditor.Utils;
import cc.buddies.component.videoeditor.retriever.hardware.callback.BaseMediaCodecCallback;
import cc.buddies.component.videoeditor.retriever.hardware.callback.ExtractVideoFramesCallback;
import cc.buddies.component.videoeditor.retriever.hardware.callback.ExtractVideoSyncFramesCallback;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameFinishListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFramePreparedListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameResultListener;

import java.io.IOException;

/**
 * 硬解码视频帧解析任务
 */
public class ExtractVideoFramesTask {

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mMainHandler;
    private BaseMediaCodecCallback mMediaCodecCallback;
    private RenderScript mRenderScript;

    private MediaExtractor mExtractor;
    private MediaCodec mMediaCodec;

    // 准备配置完成回调
    public OnExtractFramePreparedListener onExtractFramePreparedListener;

    // 获取帧结果回调
    public OnExtractFrameResultListener onExtractFrameResultListener;

    // 处理结束/取消回调
    public OnExtractFrameFinishListener onExtractFrameFinishListener;

    // 只取关键帧
    public boolean onlySyncFrame = false;

    // 任务配置
    private ExtractVideoFrameConfig mConfig;

    public ExtractVideoFramesTask(@NonNull final Context context) {
        this.mRenderScript = RenderScript.create(context);
    }

    public MediaExtractor getExtractor() {
        return mExtractor;
    }

    @NonNull
    public ExtractVideoFrameConfig getConfig() {
        return mConfig;
    }

    /**
     * 配置任务
     *
     * @param filePath 文件路径
     * @throws Exception 处理异常
     */
    public void setDataSource(String filePath) throws Exception {
        mExtractor = new MediaExtractor();
        MediaFormat videoFormat = null;

        try {
            mExtractor.setDataSource(filePath);
            int trackIndex = Utils.getExtractorMediaTrackIndex(mExtractor, "video/");
            mExtractor.selectTrack(trackIndex);

            videoFormat = mExtractor.getTrackFormat(trackIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 未找到视频轨道
        if (videoFormat == null) {
            throw new Exception("not found video MediaFormat!");
        }

        // 通过视频信息，匹配当前设备硬件解码器对应名称。
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String decoderName = mediaCodecList.findDecoderForFormat(videoFormat);

        // 没有支持的解码器
        if (TextUtils.isEmpty(decoderName)) {
            throw new Exception("not supported decoder!");
        }

        initAsyncHandler();

        mConfig = new ExtractVideoFrameConfig();
        mConfig.duration = videoFormat.getLong(MediaFormat.KEY_DURATION);
        mConfig.rotation = Utils.getVideoRotation(videoFormat, filePath);

        if (mMediaCodecCallback == null) {
            if (onlySyncFrame) {
                mMediaCodecCallback = new ExtractVideoSyncFramesCallback(mExtractor, mRenderScript, mConfig);
            } else {
                mMediaCodecCallback = new ExtractVideoFramesCallback(mExtractor, mRenderScript, mConfig);
            }

            mMediaCodecCallback.setOnExtractFrameResultListener(onExtractFrameResultListener);
            mMediaCodecCallback.setOnExtractFrameFinishListener(onExtractFrameFinishListener);
        }

        // 创建并启动硬解码MediaCodec
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startMediaCodecWithThreadHandler(decoderName, videoFormat);
        } else {
            startMediaCodecWithThreadLooper(decoderName, videoFormat);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startMediaCodecWithThreadHandler(String decoderName, MediaFormat format) throws IOException {
        mMediaCodec = MediaCodec.createByCodecName(decoderName);
        mMediaCodec.setCallback(mMediaCodecCallback, mHandler);

        mMediaCodec.configure(format, null, null, 0);

        if (onExtractFramePreparedListener != null) {
            onExtractFramePreparedListener.onExtractFramePrepared(mMediaCodec, format, mConfig);
        }

        // 初始化MediaCodec.Callback配置
        mMediaCodecCallback.onConfig();
    }

    private void startMediaCodecWithThreadLooper(String decoderName, MediaFormat format) {
        mHandler.post(() -> {
            try {
                mMediaCodec = MediaCodec.createByCodecName(decoderName);
                mMediaCodec.setCallback(mMediaCodecCallback);

                mMediaCodec.configure(format, null, null, 0);

                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
                mMainHandler.post(() -> {
                    if (onExtractFramePreparedListener != null) {
                        onExtractFramePreparedListener.onExtractFramePrepared(mMediaCodec, format, mConfig);
                    }

                    // 初始化MediaCodec.Callback配置
                    mMediaCodecCallback.onConfig();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initAsyncHandler() {
        mHandlerThread = new HandlerThread("ExtractVideoFrameThread");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public void release() {
        try {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            }
            if (mExtractor != null) {
                mExtractor.release();
                mExtractor = null;
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if (mHandlerThread != null) {
                mHandlerThread.quitSafely();
                mHandlerThread = null;
            }
            if (mMainHandler != null) {
                mMainHandler.removeCallbacksAndMessages(null);
                mMainHandler = null;
            }
            if (mRenderScript != null) {
                mRenderScript.destroy();
                mRenderScript = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
