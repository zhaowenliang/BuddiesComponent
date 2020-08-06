package cc.buddies.component.common.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

/**
 * App通知栏管理
 * <pre class="prettyprint">
 * 判断是否有通知栏权限：
 *      NotificationManagerCompat.from(context).areNotificationsEnabled();
 *
 * 启动打开通知栏权限页面：
 *      Intent intent = AppNotificationManager.getSettingNotificationIntent(MainActivity.this);
 *      startActivity(intent);
 * </pre>
 */
public class AppNotificationHelper {

    @NonNull
    @SuppressLint("ObsoleteSdkInt")
    public static Intent getSettingNotificationIntent(@NonNull Context context) {
        // 跳转设置，打开通知
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {         // 5.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {           // 4.4
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        return intent;
    }

}
