package cc.buddies.component.common.utils;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 资源文件工具类
 * 通过资源文件名称，获取资源id
 *
 * @author Jenly
 */
public class ResourcesUtils {

    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRAWABLE = "drawable";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";

    /**
     * 获取资源文件的id
     */
    public static int getId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_ID);
    }

    /**
     * 获取资源文件string的id
     */
    public static int getStringId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_STRING);
    }

    /**
     * 获取资源文件drable的id
     */
    public static int getDrawableId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_DRAWABLE);
    }

    /**
     * 获取资源文件layout的id
     */
    public static int getLayoutId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_LAYOUT);
    }

    /**
     * 获取资源文件style的id
     */
    public static int getStyleId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_STYLE);
    }

    /**
     * 获取资源文件color的id
     */
    public static int getColorId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_COLOR);
    }

    /**
     * 获取资源文件dimen的id
     */
    public static int getDimenId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_DIMEN);
    }

    /**
     * 获取资源文件ainm的id
     */
    public static int getAnimId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_ANIM);
    }

    /**
     * 获取资源文件menu的id
     */
    public static int getMenuId(@NonNull Context context, @NonNull String resName) {
        return getResId(context, resName, RES_MENU);
    }

    /**
     * 获取资源文件ID
     *
     * @param context Context
     * @param resName 资源名称
     * @param defType 资源类型
     * @return 资源id
     */
    public static int getResId(@NonNull Context context, @NonNull String resName, @NonNull String defType) {
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }

}