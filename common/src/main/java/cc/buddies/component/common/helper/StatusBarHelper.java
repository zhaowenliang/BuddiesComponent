package cc.buddies.component.common.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import cc.buddies.component.common.R;
import cc.buddies.component.common.utils.ColorUtils;
import cc.buddies.component.common.utils.ContextUtils;

/**
 * 状态栏管理
 */
public class StatusBarHelper {

    /**
     * 状态栏着色
     *
     * @param context     Context
     * @param window      Window
     * @param color       状态栏颜色
     * @param isDarkColor 是否是深色状态栏，控制状态栏文字颜色。
     */
    public static void tintStatusBar(Context context, Window window, @ColorInt int color, boolean isDarkColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // android6.0以上，沉浸式状态栏，设置状态栏的颜色，状态栏文字暗色/亮色。
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | (!isDarkColor ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // android5.0以上，沉浸式状态栏，设置状态栏半透明，状态栏文字不能调整。
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            final int lollipopStatusBarColor = getLollipopStatusBarColor(context.getTheme());
            window.setStatusBarColor(lollipopStatusBarColor);
        } else {
            // android4.4以上，不能沉浸式状态栏，但是可以设置半透明。
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 状态栏着色
     *
     * @param window      Window
     * @param toolbar     Toolbar
     * @param color       状态栏颜色
     * @param isDarkColor 是否是深色状态栏，控制状态栏文字颜色。
     */
    public static void tintStatusBarColor(Window window, Toolbar toolbar, @ColorInt int color, boolean isDarkColor) {
        if (window == null || toolbar == null) return;
        Context context = toolbar.getContext();

        // 控制Toolbar在状态栏下面
        toolbar.setFitsSystemWindows(true);

        // 设置状态栏颜色
        tintStatusBar(context, window, color, isDarkColor);
    }

    /**
     * 获取Android5.0系统手机状态栏颜色
     *
     * @param theme 主题
     * @return 颜色值
     */
    public static int getLollipopStatusBarColor(@NonNull Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        int statusBarColor = typedValue.data;
        return ColorUtils.colorWithAlpha(statusBarColor, 0.1F);
    }

    /**
     * 沉浸式状态栏
     *
     * @param context    Context
     * @param isDarkMode 是否深色模式，控制状态栏文字颜色
     */
    public static void translucentStatusBar(@NonNull Context context, boolean isDarkMode) {
        Activity activity = ContextUtils.getActivity(context);
        if (activity != null) {
            Window window = activity.getWindow();

            // 设置状态栏模式及透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | (!isDarkMode ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

}
