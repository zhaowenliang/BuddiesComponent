package cc.buddies.component.common.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.buddies.component.common.utils.ContextUtils;

/**
 * 标题栏管理
 */
public class TitleBarHelper {

    // 导航图标着色
    public static void tintNavigationIconColor(Toolbar toolbar, boolean isDarkColor) {
        if (toolbar == null || toolbar.getNavigationIcon() == null) return;

        if (!isDarkColor) {
            int targetColor = Color.BLACK;
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(targetColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    /**
     * Toolbar导航图标着色
     *
     * @param toolbar 导航栏
     * @param color   待着色
     */
    public static void tintNavigationIconColor(Toolbar toolbar, int color) {
        if (toolbar == null || toolbar.getNavigationIcon() == null) return;

        Drawable upArrow = toolbar.getNavigationIcon();
        if (upArrow != null) {
            upArrow.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * 隐藏SupportActionBar
     *
     * @param context Context
     */
    public static void hideSupportActionBar(Context context) {
        Activity activity = ContextUtils.getActivity(context);
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }
    }

}
