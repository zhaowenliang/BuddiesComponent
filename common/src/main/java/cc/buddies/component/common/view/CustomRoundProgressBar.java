package cc.buddies.component.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import cc.buddies.component.common.R;
import cc.buddies.component.common.utils.DensityUtils;

/**
 * 带进度的圆形进度条，线程安全的View，可直接在线程中更新进度。
 */
public class CustomRoundProgressBar extends View {

    /**
     * 画笔对象的引用
     */
    private Paint defaultRoundPaint;
    private Paint roundPaint;
    private Paint textPaint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int roundStyle;

    /**
     * 圆环范围
     */
    private RectF oval;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    // 圆环默认颜色
    private static final int DEFAULT_ROUND_COLOR = Color.LTGRAY;
    // 进度文字默认颜色
    private static final int DEFAULT_ROUND_TEXT_COLOR = Color.WHITE;
    // 进度默认最大值
    private static final int DEFAULT_PROGRESS_MAX = 100;


    public CustomRoundProgressBar(Context context) {
        this(context, null);
    }

    public CustomRoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int defaultTextSize = DensityUtils.dp2px(context, 14);
        int defaultRoundWidth = DensityUtils.dp2px(context, 3);

        // 获取主题色
        int defaultRoundProgressColor = Color.WHITE;
        TypedValue typedValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            defaultRoundProgressColor = typedValue.data;
        }

        // 获取自定义属性和默认值
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRoundProgressBar);
        roundColor = mTypedArray.getColor(R.styleable.CustomRoundProgressBar_roundProgress_roundColor, DEFAULT_ROUND_COLOR);
        roundProgressColor = mTypedArray.getColor(R.styleable.CustomRoundProgressBar_roundProgress_color, defaultRoundProgressColor);
        textColor = mTypedArray.getColor(R.styleable.CustomRoundProgressBar_roundProgress_textColor, DEFAULT_ROUND_TEXT_COLOR);
        textSize = mTypedArray.getDimension(R.styleable.CustomRoundProgressBar_roundProgress_textSize, defaultTextSize);
        roundWidth = mTypedArray.getDimension(R.styleable.CustomRoundProgressBar_roundProgress_width, defaultRoundWidth);
        max = mTypedArray.getInteger(R.styleable.CustomRoundProgressBar_roundProgress_max, DEFAULT_PROGRESS_MAX);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.CustomRoundProgressBar_roundProgress_textIsDisplayable, true);
        roundStyle = mTypedArray.getInt(R.styleable.CustomRoundProgressBar_roundProgress_style, STROKE);
        mTypedArray.recycle();
        initPaint();
    }

    private void initPaint(){
        defaultRoundPaint = new Paint();
        defaultRoundPaint.setColor(roundColor);                         // 设置圆环的颜色
        defaultRoundPaint.setStyle(Paint.Style.STROKE);                 // 设置空心
        defaultRoundPaint.setStrokeWidth(roundWidth);                   // 设置圆环的宽度
        defaultRoundPaint.setAntiAlias(true); // 消除锯齿

        roundPaint = new Paint();
        switch (roundStyle) {
            case STROKE:
                roundPaint.setStyle(Paint.Style.STROKE);
                roundPaint.setStrokeWidth(roundWidth + 1);       // 设置圆环的宽度(加1是为了完整覆盖底部圆环，防止出现带锯齿的白边)
                break;
            case FILL:
                roundPaint.setStrokeWidth(roundWidth);           // 设置圆环的宽度
                roundPaint.setStyle(Paint.Style.FILL);
                break;
        }
        roundPaint.setColor(roundProgressColor);         // 设置进度的颜色
        roundPaint.setAntiAlias(true); // 消除锯齿

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(0);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画最外层的大圆环
        int centre = getWidth() / 2;                        // 获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2);       // 圆环的半径
        canvas.drawCircle(centre, centre, radius, defaultRoundPaint);   // 画出圆环

        // 画文字进度百分比
        int percent = (int) (((float) progress / (float) max) * 100);   // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = textPaint.measureText(percent + "%");             // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if (textIsDisplayable && roundStyle == STROKE) {
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float y = centre + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
            canvas.drawText(percent + "%", centre - textWidth / 2, y, textPaint);       // 画出进度百分比
        }

        // 根据进度是实心还是空心，绘制进度
        switch (roundStyle) {
            case STROKE: {
                // 用于定义的圆弧的形状和大小的界限
                if (oval == null) {
                    oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
                }
                // 画圆环的进度
                canvas.drawArc(oval, 0, 360F * progress / max, false, roundPaint);        // 根据进度画圆弧
                break;
            }
            case FILL: {
                // 用于定义的圆弧的形状和大小的界限
                if (oval == null) {
                    // noinspection SuspiciousNameCombination
                    oval = new RectF(roundWidth, roundWidth, centre * 2 - roundWidth, centre * 2 - roundWidth);
                }
                // 画进度
                if (progress != 0) {
                    canvas.drawArc(oval, 0, 360F * progress / max, true, roundPaint);     // 根据进度画圆弧
                }
                break;
            }
        }
    }

    /**
     * 获取进度最大值
     *
     * @return 进度最大值
     */
    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max 最大进度值
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return 进度
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress 进度
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }

        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    /**
     * 获取圆环颜色
     *
     * @return 颜色值
     */
    public int getCircleColor() {
        return roundColor;
    }

    /**
     * 设置圆环颜色
     *
     * @param circleColor 颜色值
     */
    public void setCircleColor(int circleColor) {
        this.roundColor = circleColor;
        if(defaultRoundPaint != null){
            defaultRoundPaint.setColor(roundColor);
            postInvalidate();
        }
    }

    /**
     * 获取进度颜色
     *
     * @return 进度颜色值
     */
    public int getCircleProgressColor() {
        return roundProgressColor;
    }

    /**
     * 设置进度颜色
     *
     * @param circleProgressColor 颜色值
     */
    public void setCircleProgressColor(int circleProgressColor) {
        this.roundProgressColor = circleProgressColor;
        if(roundPaint != null) {
            roundPaint.setColor(roundProgressColor);
            postInvalidate();
        }
    }

    /**
     * 获取进度文字颜色
     *
     * @return 颜色值
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置进度文字颜色
     *
     * @param textColor 颜色值
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if(textPaint != null){
            textPaint.setColor(textColor);
            postInvalidate();
        }
    }

    /**
     * 获取进度文字大小
     *
     * @return text size in pixel units.
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * 获取进度文字大小
     *
     * @param textSize text size in pixel units.
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        if(textPaint != null) {
            textPaint.setTextSize(textSize);
            postInvalidate();
        }
    }

    /**
     * 获取进度圆环宽度
     *
     * @return 圆环宽度
     */
    public float getRoundWidth() {
        return roundWidth;
    }

    /**
     * 设置进度圆环宽度
     *
     * @param roundWidth 圆环宽度
     */
    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
        if(defaultRoundPaint != null) {
            defaultRoundPaint.setStrokeWidth(roundWidth);                   // 设置圆环的宽度
        }

        if (roundPaint != null) {
            switch (roundStyle) {
                case STROKE:
                    roundPaint.setStrokeWidth(roundWidth + 1);       // 设置圆环的宽度(加1是为了完整覆盖底部圆环，防止出现带锯齿的白边)
                    break;
                case FILL:
                    roundPaint.setStrokeWidth(roundWidth);           // 设置圆环的宽度
                    break;
            }
        }
        postInvalidate();
    }

    public boolean isTextIsDisplayable() {
        return textIsDisplayable;
    }

    public void setTextIsDisplayable(boolean textIsDisplayable) {
        this.textIsDisplayable = textIsDisplayable;
    }
}
