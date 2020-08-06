package cc.buddies.component.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static boolean requestPermissionIfNeeded(Activity activity, int requestCode, String... permissions) {
        List<String> lackPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!checkSelfPermission(activity, permission)) {
                lackPermissions.add(permission);
            }
        }

        if (!lackPermissions.isEmpty()) {
            requestPermissions(activity, requestCode, lackPermissions.toArray(new String[0]));
            return true;
        }
        return false;
    }

    public static void requestOverlaysIfNeeded(Activity activity, String reason, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            Toast.makeText(activity, reason, Toast.LENGTH_LONG).show();
            activity.startActivityForResult(serviceIntent, requestCode);
        }
    }

    /**
     * 检测权限是否获取
     *
     * @param context    Context
     * @param permission 权限名称
     * @return boolean
     */
    private static boolean checkSelfPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     *
     * @param activity    Activity
     * @param requestCode 请求码
     * @param permissions 权限数组
     */
    private static void requestPermissions(Activity activity, int requestCode, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

}
