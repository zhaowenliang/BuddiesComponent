package cc.buddies.component.videoeditor.cut.processor;

import android.util.Log;

public class VideoProgressAve {

    private static final String TAG = "VideoProgressAve";

    private float mVideoProgress;
    private float mAudioProgress;
    private int mStartTimeMs;
    private int mEndTimeMs;
    private VideoProgressListener mListener;

    public VideoProgressAve(VideoProgressListener listener) {
        this.mListener = listener;
    }

    public void setVideoTimeStamp(long timeStampUs) {
        Log.d(TAG, "VideoTimeStamp:" + timeStampUs);
        if (this.mListener != null) {
            // 视频解码将帧数据放入surface渲染的时候已经减去开始时间，此处不需要再减去开始时间。
            // this.mVideoProgress = (timeStampUs / 1000F - this.mStartTimeMs) / (this.mEndTimeMs - this.mStartTimeMs);
            this.mVideoProgress = (timeStampUs / 1000F) / (this.mEndTimeMs - this.mStartTimeMs);
            this.mVideoProgress = this.mVideoProgress < 0 ? 0 : this.mVideoProgress;
            this.mVideoProgress = this.mVideoProgress > 1 ? 1 : this.mVideoProgress;

            this.mListener.onProgress((this.mVideoProgress + this.mAudioProgress) / 2);
        }
    }

    public void setAudioProgress(float audioProgress) {
        Log.d(TAG, "AudioProgress:" + this.mAudioProgress);
        if (this.mListener != null) {
            this.mAudioProgress = audioProgress;

            this.mListener.onProgress((this.mVideoProgress + this.mAudioProgress) / 2);
        }
    }

    public void setStartTimeMs(int startTimeMs) {
        this.mStartTimeMs = startTimeMs;
    }

    public void setEndTimeMs(int endTimeMs) {
        this.mEndTimeMs = endTimeMs;
    }

}
