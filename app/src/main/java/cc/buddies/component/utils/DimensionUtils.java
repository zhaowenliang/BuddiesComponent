package cc.buddies.component.utils;

import android.content.Context;
import android.util.TypedValue;

public class DimensionUtils {

    public static int dp2px(final Context context, final float value) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics()) + 0.5f);
    }

}
