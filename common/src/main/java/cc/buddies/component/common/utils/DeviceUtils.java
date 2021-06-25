package cc.buddies.component.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * 设备信息工具
 */
public class DeviceUtils {

    /**
     * 获取设备屏幕信息
     *
     * @param context Context
     * @return DisplayMetrics
     */
    @NonNull
    public static DisplayMetrics getDisplayMetrics(@NonNull Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取手机厂商
     */
    @NonNull
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    @NonNull
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统版本号
     */
    @NonNull
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取Android系统版本
     */
    public static int getSystemSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备唯一号
     */
    @Nullable
    @SuppressLint("HardwareIds")
    public static String getAndroidId(@NonNull Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取应用版本号
     */
    public static long getAppVersionCode(@NonNull Context context) {
        long versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionCode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? pi.getLongVersionCode() : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取应用版本名称
     */
    @NonNull
    public static String getAppVersionName(@NonNull Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取设备支持的CPU架构
     *
     * @return String[]
     */
    @NonNull
    public static String[] getSupportedABIS() {
        String[] supportABIS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportABIS = Build.SUPPORTED_ABIS;
        } else {
            supportABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return supportABIS;
    }

    /**
     * 获取ActionBar高度
     *
     * @param context Context
     * @return ActionBar Height (pixels)
     */
    public static int getActionBarHeight(@NonNull Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     *
     * @param context Context
     * @return StatusBar Height (pixels)
     */
    public static int getStatusBarHeight(@NonNull Context context) {
        int result = 0;
        String key = "status_bar_height";
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    return Math.round(sizeOne * densityTwo / densityOne);
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }

    /**
     * 获取手机型号
     */
    @NonNull
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    @NonNull
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

}
