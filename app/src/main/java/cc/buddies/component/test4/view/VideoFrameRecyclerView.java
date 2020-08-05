package cc.buddies.component.test4.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 视频帧列表RecyclerView
 */
public class VideoFrameRecyclerView extends RecyclerView {

    private OnSlideListener mOnSlideListener;

    // 抛掷速度的缩放因子(控制列表滑动速度)
    private static final double FLING_SCALE = 0;

    public VideoFrameRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public VideoFrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoFrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new MyOnScrollListener());
        setOnTouchListener(new MyOnTouchListener());
    }

    public void setOnSlideListener(OnSlideListener slideListener) {
        this.mOnSlideListener = slideListener;
    }

    // 列表滑动投掷速度控制
    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= FLING_SCALE;
        return super.fling(velocityX, velocityY);
    }

    /**
     * 滚动监听
     */
    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mOnSlideListener != null) {
                mOnSlideListener.onScrolled(recyclerView, dx, dy);
            }
        }
    }

    /**
     * 触摸监听
     */
    private class MyOnTouchListener implements OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnSlideListener != null) {
                        mOnSlideListener.onTouchDown();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mOnSlideListener != null) {
                        mOnSlideListener.onTouchUp();
                    }
                    break;
            }
            return false;
        }
    }

    /**
     * 滑动/抬起监听
     */
    public interface OnSlideListener {
        void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy);

        void onTouchDown();

        void onTouchUp();
    }

}
