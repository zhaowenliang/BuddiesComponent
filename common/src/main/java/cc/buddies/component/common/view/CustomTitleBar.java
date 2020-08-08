package cc.buddies.component.common.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import cc.buddies.component.common.R;

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
        addCenterTitle();

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

    protected void addCenterTitle() {
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
    }

}
