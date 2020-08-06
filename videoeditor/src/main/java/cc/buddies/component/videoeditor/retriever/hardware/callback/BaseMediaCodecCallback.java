package cc.buddies.component.videoeditor.retriever.hardware.callback;

import android.media.MediaCodec;
import android.os.Build;

import androidx.annotation.RequiresApi;

import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameFinishListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameResultListener;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class BaseMediaCodecCallback extends MediaCodec.Callback {

    // 获取帧结果回调
    OnExtractFrameResultListener onExtractFrameResultListener;

    // 处理结束/取消回调
    OnExtractFrameFinishListener onExtractFrameFinishListener;

    public void setOnExtractFrameResultListener(final OnExtractFrameResultListener onExtractFrameResultListener) {
        this.onExtractFrameResultListener = onExtractFrameResultListener;
    }

    public void setOnExtractFrameFinishListener(final OnExtractFrameFinishListener onExtractFrameFinishListener) {
        this.onExtractFrameFinishListener = onExtractFrameFinishListener;
    }

    public abstract void onConfig();

}
