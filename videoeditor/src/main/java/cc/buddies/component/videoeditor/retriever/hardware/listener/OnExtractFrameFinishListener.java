package cc.buddies.component.videoeditor.retriever.hardware.listener;

public interface OnExtractFrameFinishListener {

    /**
     * 处理结束
     *
     * @param isCancelled true：取消处理，false：处理结束。
     */
    void onExtractFrameFinish(boolean isCancelled);

}
