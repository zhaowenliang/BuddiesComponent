package cc.buddies.component.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 应用上下文Context工具
 */
public class ContextUtils {

    /**
     * 判断Fragment是否已添加
     *
     * @param fragment Fragment
     * @return boolean
     */
    public static boolean isFragmentAdded(@Nullable Fragment fragment) {
        return fragment != null && fragment.isAdded() && !fragment.isDetached();
    }

    /**
     * 判断上下文是否存在
     *
     * @param ctx Context
     * @return boolean
     */
    public static boolean isContextExist(@Nullable Context ctx) {
        Activity activity = getActivity(ctx);
        return activity != null && !activity.isFinishing();
    }

    @Nullable
    public static Activity getActivity(@Nullable Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
