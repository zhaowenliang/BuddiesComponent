package cc.buddies.component.videoeditor.retriever.hardware.callback;

import android.media.MediaCodec;

import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameFinishListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameResultListener;

public abstract class BaseMediaCodecCallback extends MediaCodec.Callback {

    // 获取帧结果回调
    public OnExtractFrameResultListener onExtractFrameResultListener;

    // 处理结束/取消回调
    public OnExtractFrameFinishListener onExtractFrameFinishListener;

    public void setOnExtractFrameResultListener(final OnExtractFrameResultListener onExtractFrameResultListener) {
        this.onExtractFrameResultListener = onExtractFrameResultListener;
    }

    public void setOnExtractFrameFinishListener(final OnExtractFrameFinishListener onExtractFrameFinishListener) {
        this.onExtractFrameFinishListener = onExtractFrameFinishListener;
    }

    public abstract void onConfig();

}
