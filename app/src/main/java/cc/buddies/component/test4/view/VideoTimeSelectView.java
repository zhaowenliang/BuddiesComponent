package cc.buddies.component.test4.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.buddies.component.R;


/**
 * 视频时间编辑自定义控件
 * <p>包含左右两个时间选择滑块，和一个视频播放进度滑块</p>
 */
public class VideoTimeSelectView extends RelativeLayout {

    // 边界滑块
    private ImageView mSeekStartBar, mSeekEndBar;

    // 进度滑块
    private ImageView mSeekProgressBar;

    // 边框画笔
    private Paint mBorderPaint, mShadowPaint;

    // 边框宽度
    private int mBorderStroke;

    // 帧预览区域宽高
    private int mViewWidth, mViewHeight;

    // 边界滑块宽度
    private int mSeekWidth;

    // 边界滑块位置
    private float mSeekStartX, mSeekEndX;

    // 边界滑块最小间距
    private float mSeekMinDistance;

    // 边界滑块区域最大时长(us)
    private long mMaxTime = DEFAULT_MAX_TIME;

    // 边界滑块区域最小时长(us)
    private long mMinTime = DEFAULT_MIN_TIME;

    // 边界滑块滑动产生的偏移时间(us)
    private long mStartOffsetTime, mEndOffsetTime;

    // 边界滑块滑动监听
    private OnSeekBarListener onSeekBarListener;

    // 进度滑块滑动监听
    private OnProgressBarListener onProgressBarListener;

    // 正在滑动滑块标识
    private boolean isSeekingStartBar = false;
    private boolean isSeekingEndBar = false;

    // 进度滑块上下突出距离
    private int mProgressMargin;

    // 进度滑块自动更新Handler处理
    private Handler mProgressHandler;

    // 进度滑块移动单位时间ms
    public static final int PROGRESS_UNIT_TIME = 100;

    // Handler 进度更新处理码
    public static final int HANDLER_PROGRESS_CODE = 100;

    // 最少选择时间(us)
    public static final long DEFAULT_MIN_TIME = 1000 * 1000;

    // 最大选择时间(us)
    public static final int DEFAULT_MAX_TIME = 60 * 1000 * 1000;


    public void setOnSeekBarListener(OnSeekBarListener onSeekBarListener) {
        this.onSeekBarListener = onSeekBarListener;
    }

    public void setOnProgressBarListener(final OnProgressBarListener onProgressBarListener) {
        this.onProgressBarListener = onProgressBarListener;
    }

    public VideoTimeSelectView(final Context context) {
        this(context, null);
    }

    public VideoTimeSelectView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTimeSelectView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoTimeSelectView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        Context context = getContext();
        int progressBarWidth = dp2px(8);
        int progressBarPadding = dp2px(2);
        this.mBorderStroke = dp2px(2);
        this.mSeekWidth = dp2px(18);
        this.mProgressMargin = dp2px(4);

        // 边界滑块
        this.mSeekStartBar = new ImageView(context);
        this.mSeekEndBar = new ImageView(context);

        LayoutParams leftLayoutParams = new LayoutParams(mSeekWidth, LayoutParams.MATCH_PARENT);
        leftLayoutParams.topMargin = mProgressMargin;
        leftLayoutParams.bottomMargin = mProgressMargin;
        leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        this.mSeekStartBar.setLayoutParams(leftLayoutParams);
        addView(this.mSeekStartBar);

        LayoutParams rightLayoutParams = new LayoutParams(mSeekWidth, LayoutParams.MATCH_PARENT);
        rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightLayoutParams.topMargin = mProgressMargin;
        rightLayoutParams.bottomMargin = mProgressMargin;
        this.mSeekEndBar.setLayoutParams(rightLayoutParams);
        addView(this.mSeekEndBar);

        this.mSeekStartBar.setImageResource(R.drawable.video_clipping_left);
        this.mSeekStartBar.setScaleType(ImageView.ScaleType.FIT_XY);
        this.mSeekEndBar.setImageResource(R.drawable.video_clipping_right);
        this.mSeekEndBar.setScaleType(ImageView.ScaleType.FIT_XY);

        // 进度滑块
        this.mSeekProgressBar = new ImageView(context);
        this.mSeekProgressBar.setImageResource(R.drawable.video_editor_seek_bar);

        // 扩大点击区域
        this.mSeekProgressBar.setPadding(progressBarPadding, 0, progressBarPadding, 0);

        LayoutParams progressBarLayoutParams = new LayoutParams(progressBarWidth + progressBarPadding * 2, LayoutParams.MATCH_PARENT);
        progressBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        progressBarLayoutParams.leftMargin = this.mSeekWidth - progressBarLayoutParams.width / 2;
        this.mSeekProgressBar.setLayoutParams(progressBarLayoutParams);
        addView(this.mSeekProgressBar);

        // 滑动事件
        this.mSeekStartBar.setOnTouchListener(new OnTouchStartBarListener());
        this.mSeekEndBar.setOnTouchListener(new OnTouchEndBarListener());
        this.mSeekProgressBar.setOnTouchListener(new OnTouchProgressBarListener());

        // 初始化边框画笔
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setColor(Color.parseColor("#FFEABD76"));
        this.mBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        // 初始化阴影画笔
        this.mShadowPaint = new Paint();
        this.mShadowPaint.setColor(Color.parseColor("#667E591E"));
        this.mShadowPaint.setStyle(Paint.Style.FILL);
        this.mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));  // SRC_OVER
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacksAndMessages(null);
            mProgressHandler = null;
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewWidth = w;
        this.mViewHeight = h;

        this.mSeekStartX = 0;
        this.mSeekEndX = mViewWidth - mSeekWidth;

        // 初始化滑块最小间距
        initSeekMinDistance();

        // 初始化选择时间
        initSeekBarTime();
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        // 绘制上下边框矩形
        canvas.drawRect(new RectF(mSeekWidth + mSeekStartX, mProgressMargin, mSeekEndX, mBorderStroke + mProgressMargin), mBorderPaint);
        canvas.drawRect(new RectF(mSeekWidth + mSeekStartX, mViewHeight - mBorderStroke - mProgressMargin, mSeekEndX, mViewHeight - mProgressMargin), mBorderPaint);
        // 更新阴影
        canvas.drawRect(new RectF(mSeekWidth + mSeekStartX, mBorderStroke + mProgressMargin, mSeekEndX, mViewHeight - mBorderStroke - mProgressMargin), mShadowPaint);

        // 如果边界滑块更新位置，同时更新进度滑块位置
        if (this.isSeekingStartBar || this.isSeekingEndBar) {
            float translationX = mSeekProgressBar.getTranslationX();
            float newTranslationX = isSeekingStartBar ? mSeekStartX : mSeekEndX - mSeekWidth;

            if (translationX != newTranslationX) {
                mSeekProgressBar.setTranslationX(newTranslationX);
            }
        }
    }

    /**
     * 设定可选择最大时长
     *
     * @param maxTime us
     */
    public void setSelectMaxTime(long maxTime) {
        this.mMaxTime = maxTime;
    }

    /**
     * 设定可选择最小时长
     *
     * @param minTime us
     */
    public void setSelectMinTime(long minTime) {
        this.mMinTime = minTime;
    }

    /**
     * 获取边界滑块宽度
     */
    public int getSeekBarWidth() {
        return this.mSeekWidth;
    }

    /**
     * 获取选择区域最大宽度
     */
    public int getSelectedMaxWidth() {
        return mViewWidth - 2 * mSeekWidth;
    }

    /**
     * 获取当前选择区域宽度
     */
    public float getSelectedWidth() {
        return mSeekEndX - mSeekStartX - mSeekWidth;
    }

    /**
     * 获取选择总时长
     */
    public long getTotalTime() {
        return mEndOffsetTime - mStartOffsetTime;
    }

    /**
     * 获取进度百分比
     */
    public float getProgressPercent() {
        float transWidth = mSeekProgressBar.getTranslationX() - mSeekStartX;
        float totalWidth = getSelectedWidth();
        return transWidth / totalWidth;
    }

    // 初始化滑块最小间距
    private void initSeekMinDistance() {
        this.mSeekMinDistance = getSelectedMaxWidth() / (mMaxTime / (float) mMinTime);
    }

    // 初始化滑块选择时间及回调
    private void initSeekBarTime() {
        // 回调边界滑块位置
        if (this.onSeekBarListener != null) {
            int selectedMaxWidth = getSelectedMaxWidth();

            // 边界滑块占据时间选择区位置百分比
            float startOffsetPercent = mSeekStartX / selectedMaxWidth;
            float endOffsetPercent = (mSeekEndX - mSeekWidth) / selectedMaxWidth;

            // 边界滑块相对左侧起始时间偏移时间量
            this.mStartOffsetTime = (long) (mMaxTime * startOffsetPercent);
            this.mEndOffsetTime = (long) (mMaxTime * endOffsetPercent);

            long totalTime = getTotalTime();
            this.onSeekBarListener.onSeekTime(totalTime);

            if (this.isSeekingStartBar) {
                this.onSeekBarListener.onStartSeekBarSlide(mStartOffsetTime);
            }
            if (this.isSeekingEndBar) {
                this.onSeekBarListener.onEndSeekBarSlide(mEndOffsetTime);
            }
        }
    }

    // 回调进度滑块变更监听
    private void callProgressBarChangeListener(boolean isUser) {
        if (this.onProgressBarListener != null) {
            long totalTime = getTotalTime();
            float progressPercent = getProgressPercent();

            this.onProgressBarListener.onProgressBarChanged(mSeekProgressBar, totalTime, progressPercent, isUser);
        }
    }

    /**
     * 播放器进度播放状态
     */
    public void onProgressStart() {
        if (mProgressHandler == null) {
            mProgressHandler = new Handler(new ProgressHandlerCallback());
        }
        mProgressHandler.obtainMessage(HANDLER_PROGRESS_CODE).sendToTarget();
    }

    /**
     * 播放器进度暂停状态
     */
    public void onProgressPause() {
        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 重置播放进度
     */
    public void onProgressRest() {
        onProgressPause();

        // 重置进度条位置
        if (this.mSeekProgressBar.getTranslationX() != this.mSeekStartX) {
            this.mSeekProgressBar.setTranslationX(this.mSeekStartX);
        }
    }

    /**
     * 进度滑块进度更新
     */
    private class ProgressHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(final Message msg) {
            // 相对起始滑块偏移时间量 us
            long videoOffsetPosition = onProgressBarListener.getVideoOffsetPosition();
            if (videoOffsetPosition < 0) videoOffsetPosition = 0;

            float curMaxOffset = getSelectedWidth();
            long curMaxOffsetTime = getTotalTime();
            float videoOffsetPositionPercent = videoOffsetPosition / (float) curMaxOffsetTime;
            if (videoOffsetPositionPercent > 1) videoOffsetPositionPercent = 1F;

            // 相对起始滑块偏移距离
            float offset = curMaxOffset * videoOffsetPositionPercent;

            float transX = mSeekProgressBar.getTranslationX();
            float newTransX = mSeekStartX + offset;

            // 达到边界，强制回调进度改变方法
            boolean focusCallback = false;

            // 进度滑块的偏移量不能小于起始滑块的偏移量
            if (newTransX < mSeekStartX) {
                newTransX = mSeekStartX;
                focusCallback = true;
            }

            // 当前滑块的偏移量不能大于结束滑块的位置
            if (newTransX >= (mSeekEndX - mSeekWidth)) {
                newTransX = (mSeekEndX - mSeekWidth);
                focusCallback = true;
            }

            if (transX != newTransX || focusCallback) {
                if (transX != newTransX) {
                    mSeekProgressBar.setTranslationX(newTransX);
                }
                // 更新进度回调
                callProgressBarChangeListener(false);
            }

            // 持续更新
            if (mProgressHandler != null) {
                mProgressHandler.postDelayed(() -> mProgressHandler.obtainMessage(HANDLER_PROGRESS_CODE).sendToTarget(), PROGRESS_UNIT_TIME);
            }
            return true;
        }
    }

    /**
     * 起始滑块滑动触发事件分发
     */
    private class OnTouchStartBarListener implements OnTouchListener {
        private float mDownX;   // 按下滑块的坐标

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:       // 按下滑块
                    mDownX = event.getX();
                    isSeekingStartBar = true;
                    isSeekingEndBar = false;

                    if (onSeekBarListener != null) {
                        onSeekBarListener.onStartSeekBarTouch();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:       // 滑动左滑块
                    // 滑动偏移量
                    float xDistance = event.getX() - mDownX;
                    if (xDistance != 0F) {
                        float transX = v.getTranslationX();
                        float newTransX = transX + xDistance;
                        float startBorder = 0F;
                        float endBorder = mSeekEndX - mSeekMinDistance - v.getWidth();

                        // 滑块偏移量不能小于左边界
                        if (newTransX < startBorder) {
                            newTransX = startBorder;
                        }

                        // 滑块偏移量不能大于右边界
                        if (newTransX > endBorder) {
                            newTransX = endBorder;
                        }

                        // 没有改变位置则不刷新
                        if (transX == newTransX) {
                            break;
                        }

                        // 更新位置
                        v.setTranslationX(newTransX);
                        mSeekStartX = v.getLeft() + newTransX;
                        // 更新阴影
                        invalidate();
                        // 滑块偏移时间
                        initSeekBarTime();
                    }

                    break;
                case MotionEvent.ACTION_UP:         // 滑动抬起
                case MotionEvent.ACTION_CANCEL:     // 滑动取消
                    isSeekingStartBar = false;
                    if (onSeekBarListener != null) {
                        onSeekBarListener.onStartSeekBarRelease();
                    }
                    break;
            }

            return true;
        }
    }

    /**
     * 结束滑块滑动触发事件分发
     */
    private class OnTouchEndBarListener implements OnTouchListener {
        private float mDownX;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:       // 按下滑块
                    mDownX = event.getX();
                    isSeekingEndBar = true;
                    isSeekingStartBar = false;

                    if (onSeekBarListener != null) {
                        onSeekBarListener.onEndSeekBarTouch();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:       // 滑动右滑块
                    // 滑动偏移量
                    float xDistance = event.getX() - mDownX;
                    if (xDistance != 0F) {
                        float transX = v.getTranslationX();
                        float newTransX = transX + xDistance;
                        float startBorder = -(getSelectedMaxWidth() - mSeekMinDistance - mSeekStartX);
                        float endBorder = 0F;

                        // 滑块偏移量不能大于右边界
                        if (newTransX > endBorder) {
                            newTransX = endBorder;
                        }

                        // 滑块偏移量不能小于左边界
                        if (newTransX < startBorder) {
                            newTransX = startBorder;
                        }

                        // 没有改变位置则不刷新
                        if (transX == newTransX) {
                            break;
                        }

                        // 更新位置
                        v.setTranslationX(newTransX);
                        mSeekEndX = v.getLeft() + newTransX;
                        // 更新阴影
                        invalidate();
                        // 滑块偏移时间
                        initSeekBarTime();
                    }

                    break;
                case MotionEvent.ACTION_UP:         // 滑动抬起
                case MotionEvent.ACTION_CANCEL:     // 滑动取消
                    isSeekingEndBar = false;
                    if (onSeekBarListener != null) {
                        onSeekBarListener.onEndSeekBarRelease();
                    }
                    break;
            }

            return true;
        }
    }

    /**
     * 进度滑块触发事件分发
     */
    private class OnTouchProgressBarListener implements OnTouchListener {
        private float mDownX;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:       // 按下滑块
                    mDownX = event.getX();
                    isSeekingStartBar = false;
                    isSeekingEndBar = false;

                    if (onProgressBarListener != null) {
                        onProgressBarListener.onProgressBarTrackingTouch(v);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:       // 滑动滑块
                    // 滑动偏移量
                    float xDistance = event.getX() - mDownX;
                    if (xDistance != 0F) {
                        // 进度滑块偏移必须在两个边界滑块内
                        float transX = v.getTranslationX();
                        float newTransX = transX + xDistance;

                        // 进度滑块的偏移量不能小于起始滑块的偏移量
                        if (newTransX < mSeekStartX) {
                            newTransX = mSeekStartX;
                        }

                        // 当前滑块的偏移量不能大于结束滑块的位置
                        if (newTransX > mSeekEndX - mSeekWidth) {
                            newTransX = mSeekEndX - mSeekWidth;
                        }

                        // 没有改变位置则不刷新
                        if (transX == newTransX) {
                            break;
                        }

                        // 更新位置
                        v.setTranslationX(newTransX);
                        // 回调进度更新
                        callProgressBarChangeListener(true);
                    }

                    break;
                case MotionEvent.ACTION_UP:         // 滑动抬起
                case MotionEvent.ACTION_CANCEL:     // 滑动取消
                    if (onProgressBarListener != null) {
                        onProgressBarListener.onProgressBarTrackingRelease(v);
                    }
                    break;
            }

            return true;
        }
    }

    /**
     * 边界滑块滑动事件监听
     */
    public interface OnSeekBarListener {
        /**
         * 按下起始滑块
         */
        void onStartSeekBarTouch();

        /**
         * 松开起始滑块
         */
        void onStartSeekBarRelease();

        /**
         * 滑动起始滑块
         *
         * @param offsetTime 偏移时间
         */
        void onStartSeekBarSlide(long offsetTime);

        /**
         * 按下结束滑块
         */
        void onEndSeekBarTouch();

        /**
         * 松开结束滑块
         */
        void onEndSeekBarRelease();

        /**
         * 滑动结束滑块
         *
         * @param offsetTime 偏移时间
         */
        void onEndSeekBarSlide(long offsetTime);

        /**
         * 已选择时长
         *
         * @param totalTime 选择时长
         */
        void onSeekTime(long totalTime);
    }

    /**
     * 滑块滑动回调
     */
    public interface OnProgressBarListener {
        /**
         * 滑动中
         *
         * @param view            滑块View
         * @param totalTime       选择区域总时长
         * @param progressPercent 进度百分比
         * @param fromUser        是否由手动操作
         */
        void onProgressBarChanged(View view, long totalTime, float progressPercent, boolean fromUser);

        /**
         * 按下滑块
         *
         * @param view 滑块View
         */
        void onProgressBarTrackingTouch(View view);

        /**
         * 抬起滑块
         *
         * @param view 滑块View
         */
        void onProgressBarTrackingRelease(View view);

        /**
         * 获取当前播放器相对 时间选择区域起始时间点 播放进度
         *
         * @return us
         */
        long getVideoOffsetPosition();
    }


    private int dp2px(float value) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()) + 0.5F);
    }

}
