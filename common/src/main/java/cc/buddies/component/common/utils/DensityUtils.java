package cc.buddies.component.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

/**
 * 设备尺寸单位转换工具
 */
public class DensityUtils {

    public static int dp2px(@NonNull Context context, float value) {
        return (int) trans(context, TypedValue.COMPLEX_UNIT_DIP, value);
    }

    public static int sp2px(@NonNull Context context, float value) {
        return (int) trans(context, TypedValue.COMPLEX_UNIT_SP, value);
    }

    public static float px2dp(@NonNull Context context, float value) {
        return value / getDisplayMetrics(context).density;
    }

    public static float px2sp(@NonNull Context context, float value) {
        return value / getDisplayMetrics(context).scaledDensity;
    }

    private static float trans(@NonNull Context context, int unit, float value) {
        return TypedValue.applyDimension(unit, value, getDisplayMetrics(context)) + 0.5f;
    }

    private static DisplayMetrics getDisplayMetrics(@NonNull Context context) {
        return context.getResources().getDisplayMetrics();
    }

}
