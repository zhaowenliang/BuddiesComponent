package cc.buddies.component.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ApplicationUtils {

    /**
     * 获取清单文件配置的MetaData值
     *
     * @param context Context
     * @param metaKey meta对应的键
     * @return meta data
     */
    @Nullable
    public static String getMetaData(@NonNull Context context, String metaKey) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定pid对应的进程名称
     *
     * @param context Context
     * @param pid     进程id
     * @return 进程名称
     */
    @Nullable
    public static String getProcessName(@NonNull Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
