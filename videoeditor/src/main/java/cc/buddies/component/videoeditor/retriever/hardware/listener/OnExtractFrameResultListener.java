package cc.buddies.component.videoeditor.retriever.hardware.listener;

import android.graphics.Bitmap;

public interface OnExtractFrameResultListener {

    /**
     * 处理结果
     *
     * @param totalCount 总数量
     * @param time       帧时间(us)
     * @param bitmap     帧图片
     */
    void onExtractFrameResult(int totalCount, long time, Bitmap bitmap);

}
