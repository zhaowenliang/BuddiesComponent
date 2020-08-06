package cc.buddies.component.common.provider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Android7.0后Uri.fromFile(file)报错，需要虚拟文件目录共享。
 * Created by zhaowl on 2018/3/6.
 */
public class CommonFileProvider {

    /**
     * 设置Intent打开共享文件配置
     *
     * @param context Context
     * @param intent  Intent
     * @param file    File
     */
    public static void makeIntentDataAndType(Context context, Intent intent, File file) {
        Uri uri = getUriForFile(context, file);
        String type = context.getContentResolver().getType(uri);

        intent.setDataAndType(uri, type);

        // 授予临时读取文件权限。
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    /**
     * 根据文件路径生成Uri
     *
     * @param context Context
     * @param file    File
     * @return Uri
     */
    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        return Build.VERSION.SDK_INT >= 24 ? getUriForFile24(context, file) : Uri.fromFile(file);
    }

    /**
     * Android7.0以后共享文件需要生成虚拟目录
     *
     * @param context Context
     * @param file    File
     * @return Uri
     */
    public static Uri getUriForFile24(@NonNull Context context, @NonNull File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".file-provider", file);
    }

}
