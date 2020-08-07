package cc.buddies.component.material.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import cc.buddies.component.material.R;
import cc.buddies.component.material.utils.ContextUtils;

/**
 * 自定义标题居中的Toolbar
 */
public class CustomTitleBar extends Toolbar {

    private AppCompatTextView mTitleTextView;

    private int mTitleTextAppearance;
    private ColorStateList mTitleTextColor;
    private CharSequence mTitleText;

    public CustomTitleBar(Context context) {
        this(context, null);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义标题文本样式
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar, defStyleAttr, 0);
        mTitleTextAppearance = typedArray.getResourceId(R.styleable.CustomTitleBar_titleTextAppearance, 0);

        final CharSequence title = typedArray.getText(R.styleable.CustomTitleBar_titleText);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        typedArray.recycle();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(null);
        if (mTitleTextView == null) {
            final Context context = getContext();
            mTitleTextView = new AppCompatTextView(context);
            mTitleTextView.setSingleLine();
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            if (mTitleTextAppearance != 0) {
                mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
            }
            if (mTitleTextColor != null) {
                mTitleTextView.setTextColor(mTitleTextColor);
            }

            // 加入布局
            final LayoutParams layoutParams = generateDefaultLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            mTitleTextView.setLayoutParams(layoutParams);
            addView(mTitleTextView);
        }

        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    @Override
    public CharSequence getTitle() {
        return this.mTitleText;
    }

    @Override
    public void setTitleTextColor(@NonNull ColorStateList color) {
        super.setTitleTextColor(color);
        this.mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    /**
     * 如果背景是颜色，则着色状态栏。
     *
     * @param background 背景
     */
    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            tintStatusBarColor(getContextWindow(), colorDrawable.getColor());
        }
    }

    /**
     * 背景着色的同时着色状态栏。
     *
     * @param tint 着色
     */
    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        super.setBackgroundTintList(tint);
        if (tint != null) {
            tintStatusBarColor(getContextWindow(), tint.getDefaultColor());
        }
    }

    /**
     * 获取当前上下文所在窗口window
     */
    @Nullable
    private Window getContextWindow() {
        final Activity activity = ContextUtils.getActivity(getContext());
        return activity != null ? activity.getWindow() : null;
    }

    /**
     * 着色状态栏
     *
     * @param window 当前所在窗口window
     * @param color  颜色值
     */
    private void tintStatusBarColor(Window window, @ColorInt int color) {
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

}
