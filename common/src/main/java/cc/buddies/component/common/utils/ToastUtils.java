package cc.buddies.component.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import cc.buddies.component.common.R;

public class ToastUtils {

    private static Toast mToast;

    public static void shortToast(@NonNull Context context, @StringRes int strResId) {
        final String content = context.getString(strResId);
        toast(context, content, Toast.LENGTH_SHORT);
    }

    public static void shortToast(@NonNull Context context, CharSequence content) {
        toast(context, content, Toast.LENGTH_SHORT);
    }

    public static void longToast(@NonNull Context context, @StringRes int strResId) {
        final String content = context.getString(strResId);
        toast(context, content, Toast.LENGTH_LONG);
    }

    public static void longToast(@NonNull Context context, String content) {
        toast(context, content, Toast.LENGTH_LONG);
    }

    /**
     * 弹出吐司
     *
     * @param content  内容
     * @param duration 持续时长 {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public static void toast(@NonNull Context context, CharSequence content, int duration) {
        toast(context, content, duration, Gravity.CENTER, 0, 0);
    }

    /**
     * 弹出吐司
     *
     * @param content  内容
     * @param duration 持续时长 {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     * @param gravity  显示位置{@link Toast#LENGTH_SHORT}
     * @param xOffset  偏移x
     * @param yOffset  偏移y
     */
    public static void toast(@NonNull Context context, CharSequence content, int duration, int gravity, int xOffset, int yOffset) {
        if (TextUtils.isEmpty(content)) return;

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, content, duration);
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.setGravity(gravity, xOffset, yOffset);

        modifyToastStyle();
        mToast.show();
    }

    private static void modifyToastStyle() {
        final TextView textView = mToast.getView().findViewById(android.R.id.message);
        if (textView != null && textView.getParent() instanceof ViewGroup) {
            // 设置背景样式
            final ViewGroup parent = (ViewGroup) textView.getParent();
            parent.setBackgroundResource(R.drawable.custom_toast_background);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.topMargin = 0;
            params.bottomMargin = 0;
            params.leftMargin = 0;
            params.rightMargin = 0;

            // 设置文字样式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(R.style.CustomToastTextStyle);
            } else {
                textView.setTextColor(Color.WHITE);
            }
        }
    }

}
