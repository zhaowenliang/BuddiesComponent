package cc.buddies.component.material.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;

/**
 * 应用上下文Context工具
 */
public class ContextUtils {

    /**
     * 判断上下文是否存在
     *
     * @param ctx Context
     * @return boolean
     */
    public static boolean isContextExist(Context ctx) {
        Context context = ctx;

        // Activity的Context是继承ContextThemeWrapper，所有优先判断是否是Activity。
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing();
        }

        // Dialog构造会将传入Context包装为ContextThemeWrapper类，其内部BaseContext为原始上下文。
        if (context instanceof ContextThemeWrapper) {
            context = ((ContextThemeWrapper) context).getBaseContext();
        }

        if (context instanceof Activity) {
            return !((Activity) context).isFinishing();
        } else {
            return context instanceof Application;
        }
    }

    /**
     * Returns the {@link Activity} given a {@link Context} or null if there is no {@link Activity},
     * taking into account the potential hierarchy of {@link ContextWrapper ContextWrappers}.
     *
     * @see com.google.android.material.internal.ContextUtils#getActivity(Context)
     */
    @Nullable
    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
