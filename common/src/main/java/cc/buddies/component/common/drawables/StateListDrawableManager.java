package cc.buddies.component.common.drawables;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.ColorInt;

/**
 * 创建selector drawable/color
 */
public class StateListDrawableManager {

    private static final int STATE_PRESSED = android.R.attr.state_pressed;
    private static final int STATE_FOCUSED = android.R.attr.state_focused;
    private static final int STATE_ENABLED = android.R.attr.state_enabled;
    private static final int STATE_SELECTED = android.R.attr.state_selected;
    private static final int STATE_CHECKED = android.R.attr.state_checked;
    private static final int STATE_WINDOW_FOCUSED = android.R.attr.state_window_focused;

    private Builder mBuilder;

    private StateListDrawableManager(Builder builder) {
        this.mBuilder = builder;
    }

    public static Builder with() {
        return new Builder();
    }

    /**
     * 创建color selector
     * @return ColorStateList
     */
    public ColorStateList createColorStateList() {
        int pressed = mBuilder.pressed;
        int focused = mBuilder.focused;
        int enabled = mBuilder.enabled;
        int unabled = mBuilder.unabled;
        int selected = mBuilder.selected;
        int checked = mBuilder.checked;
        int windowFocused = mBuilder.windowFocused;

        int[] colors = new int[]{pressed, focused, enabled, unabled, selected, checked, windowFocused, enabled};
        int[][] states = new int[8][];

        states[0] = new int[]{STATE_PRESSED};           // 按下
        states[1] = new int[]{STATE_FOCUSED};           // 焦点
        states[2] = new int[]{STATE_ENABLED};           // 可用/正常状态
        states[3] = new int[]{-STATE_ENABLED};          // 不可用
        states[4] = new int[]{STATE_SELECTED};          // 定向控件浏览(例如方向键浏览列表)
        states[5] = new int[]{STATE_CHECKED};           // 选中
        states[6] = new int[]{STATE_WINDOW_FOCUSED};    // 应用窗口有焦点(通知栏下拉或对话框出现失去焦点)
        states[7] = new int[]{};

        return new ColorStateList(states, colors);
    }

    /**
     * 创建drawable selector
     * @return StateListDrawable
     */
    public StateListDrawable createStateListDrawable() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, mBuilder.gradientDrawablePressed);
        drawable.addState(new int[]{-android.R.attr.state_pressed}, mBuilder.gradientDrawable);
        return drawable;
    }

    public static class Builder {
        int pressed;            // 按下颜色
        int focused;            // 焦点颜色
        int enabled;            // 可用/正常状态颜色
        int unabled;            // 不可用状态颜色
        int selected;           // 定向控件浏览选择颜色
        int checked;            // 选中颜色
        int windowFocused;      // 应用窗口有焦点时颜色

        GradientDrawable gradientDrawable;              // 未点击状态样式
        GradientDrawable gradientDrawablePressed;       // 点击状态样式

        public Builder setPressed(@ColorInt int pressed) {
            this.pressed = pressed;
            return this;
        }

        public Builder setFocused(@ColorInt int focused) {
            this.focused = focused;
            return this;
        }

        public Builder setEnabled(@ColorInt int enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setUnabled(@ColorInt int unabled) {
            this.unabled = unabled;
            return this;
        }

        public Builder setSelected(@ColorInt int selected) {
            this.selected = selected;
            return this;
        }

        public Builder setChecked(@ColorInt int checked) {
            this.checked = checked;
            return this;
        }

        public Builder setWindowFocused(@ColorInt int windowFocused) {
            this.windowFocused = windowFocused;
            return this;
        }

        public Builder setGradientDrawable(GradientDrawable gradientDrawable) {
            this.gradientDrawable = gradientDrawable;
            return this;
        }

        public Builder setGradientDrawablePressed(GradientDrawable gradientDrawablePressed) {
            this.gradientDrawablePressed = gradientDrawablePressed;
            return this;
        }

        public StateListDrawableManager build() {
            return new StateListDrawableManager(this);
        }
    }

}
