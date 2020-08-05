package cc.buddies.component.videoeditor.retriever.hardware;


public class ExtractVideoFrameConfig {

    // 视频时长(us)
    public long duration = 0;

    // 视频选择角度
    public int rotation = 0;

    // 目标宽
    public int targetWidth = 0;

    // 目标高
    public int targetHeight = 0;

    // 最大帧数
    public int maxCount = 0;

    // 帧时间间隔(us)
    public long interval = 0;

    // 开始时间(us)
    public long startTime = 0;

    // 结束时间(us)
    public long endTime = 0;

}
