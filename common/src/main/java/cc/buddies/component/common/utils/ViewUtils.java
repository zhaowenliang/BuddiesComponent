package cc.buddies.component.common.utils;

import android.app.Activity;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;

import androidx.annotation.RequiresApi;

public class ViewUtils {

    /**
     * 获取activity的根view
     */
    public static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
    }

    /**
     * 设置View圆角
     *
     * @param view   目标View
     * @param radius 圆角
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setViewOutlineProvider(View view, final int radius) {
        if (view == null) return;

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        view.setClipToOutline(true);
    }

}
