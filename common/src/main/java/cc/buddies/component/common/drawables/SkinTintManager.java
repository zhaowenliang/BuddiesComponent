package cc.buddies.component.common.drawables;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import cc.buddies.component.common.utils.ColorUtils;

/**
 * 皮肤着色管理
 */
public class SkinTintManager {

    // 不可用View透明度
    private static final float unableAlpha = 0.8f;

    // 不可用文字透明度/点击文字透明度
    private static final float unableTextAlpha = 0.4f;

    /**
     * View皮肤着色
     *
     * @param view  View
     * @param color 颜色
     */
    public static void applyTint(@NonNull View view, @ColorInt int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    /**
     * View皮肤着色，带默认透明度
     *
     * @param view  View
     * @param color 颜色
     */
    public static void applyTintWithAlpha(@NonNull View view, @ColorInt int color) {
        // 设置透明度
        int colorAlpha = ColorUtils.colorWithAlpha(color, unableAlpha);
        ColorStateList colorStateList = StateListDrawableManager.with()
                .setEnabled(color).setUnabled(colorAlpha).setPressed(colorAlpha).setFocused(color).build().createColorStateList();
        ViewCompat.setBackgroundTintList(view, colorStateList);
    }

    /**
     * TextView文本着色
     *
     * @param textView TextView
     * @param color    颜色
     */
    public static void applyTintText(@NonNull TextView textView, @ColorInt int color) {
        textView.setTextColor(ColorStateList.valueOf(color));
    }

    /**
     * TextView文本着色
     *
     * @param textView TextView
     * @param color    颜色
     */
    public static void applyTintTextWithPressAlpha(@NonNull TextView textView, @ColorInt int color) {
        int colorAlpha = ColorUtils.colorWithAlpha(color, unableTextAlpha);
        ColorStateList colorStateList = StateListDrawableManager.with()
                .setEnabled(color).setUnabled(colorAlpha).setPressed(colorAlpha).setFocused(color).build().createColorStateList();
        textView.setTextColor(colorStateList);
    }

    /**
     * TextView文本着色(正常颜色和点击颜色)
     *
     * @param textView     TextView
     * @param colorNormal  正常状态颜色
     * @param colorPressed 按下/不可用状态颜色
     */
    public static void applyTintText(@NonNull TextView textView, @ColorInt int colorNormal, @ColorInt int colorPressed) {
        ColorStateList colorStateList = StateListDrawableManager.with()
                .setEnabled(colorNormal).setUnabled(colorPressed).setPressed(colorPressed).setFocused(colorNormal).build().createColorStateList();
        textView.setTextColor(colorStateList);
    }

}
