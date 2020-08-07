package cc.buddies.component.common.storage;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import cc.buddies.component.common.R;

/**
 * 存储工具类
 * <pre>
 *     {@link #getCacheDir(Context)} /data/data/{package}/cache 目录为私有 数据/文件 缓存目录。
 *     {@link #getFilesDir(Context)} /data/data/{package}/files 目录为私有 数据/文件 文件目录。
 *     {@link #getExternalCacheDir(Context)} /Android/data/{package}/cache 目录为私有 数据/文件 公共缓存目录。
 *     {@link #getExternalFileDir(Context, String)} /Android/data/{package}/cache 目录为私有 数据/文件 公共文件目录。
 *     {@link #getExternalStorageDirectory()} /mnt/0 目录为SD卡根目录，为公有文件目录。
 *          {@link Environment#DIRECTORY_DCIM} 为拍照/录像存储公有目录(自动更新数据到媒体数据库)。
 *          {@link Environment#DIRECTORY_PICTURES} 为图片存储公有目录(自动更新数据到媒体数据库)。
 *          {@link Environment#DIRECTORY_MOVIES} 为视频存储公有目录(自动更新数据到媒体数据库)。
 * </pre>
 */
public class StorageUtils {

    /**
     * 获取app默认外部存储目录
     * <p>优先读取string资源文件中配置的app_storage_directory的值，如果为空则默认使用app包名</p>
     *
     * @param context Context
     * @return File
     */
    @Nullable
    public static File getDefaultAppExternalStorageDir(Context context) {
        if (context == null) return null;

        String storageDirName = context.getString(R.string.app_storage_directory);
        if (storageDirName.length() == 0) {
            storageDirName = context.getPackageName();
        }

        return getExternalStoragePublicDirectory(storageDirName);
    }

    /**
     * 获取app默认外部存储DCIM下目录
     * <p>该目录下存储图片/视频，会被系统自动扫描到媒体数据库。</p>
     * <p>优先读取string资源文件中配置的app_storage_directory的值，如果为空则默认使用app包名</p>
     *
     * @param context Context
     * @return File
     */
    @Nullable
    public static File getDefaultAppExternalStorageDCIM(Context context) {
        if (context == null) return null;

        File fileDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (fileDir == null) return null;

        String appPackage = context.getPackageName();
        return new File(fileDir, appPackage);
    }

    /**
     * 内置存储器挂载状态
     *
     * @return {@link Environment#MEDIA_UNKNOWN}, {@link Environment#MEDIA_REMOVED},
     * {@link Environment#MEDIA_UNMOUNTED}, {@link Environment#MEDIA_CHECKING},
     * {@link Environment#MEDIA_NOFS}, {@link Environment#MEDIA_MOUNTED},
     * {@link Environment#MEDIA_MOUNTED_READ_ONLY}, {@link Environment#MEDIA_SHARED},
     * {@link Environment#MEDIA_BAD_REMOVAL}, or {@link Environment#MEDIA_UNMOUNTABLE}.
     */
    @NonNull
    public static String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    /**
     * 内置存储器是否已经挂载
     *
     * @return boolean
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(getExternalStorageState());
    }

    /**
     * 内置存储挂载路径
     * <p>为了避免污染用户的根命名空间，不要直接使用该目录，至少再创建一层目录。</p>
     *
     * @return File 如果已经挂载存储器，则返回路径，否则返回空。
     */
    @Nullable
    public static File getExternalStorageDirectory() {
        if (isExternalStorageMounted()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 内置存储器 公开目录
     *
     * @param type 存储目录类型，不可以为空。
     *             {@link Environment#DIRECTORY_MUSIC}, {@link Environment#DIRECTORY_PODCASTS},
     *             {@link Environment#DIRECTORY_RINGTONES}, {@link Environment#DIRECTORY_ALARMS},
     *             {@link Environment#DIRECTORY_NOTIFICATIONS}, {@link Environment#DIRECTORY_PICTURES},
     *             {@link Environment#DIRECTORY_MOVIES}, {@link Environment#DIRECTORY_DOWNLOADS},
     *             {@link Environment#DIRECTORY_DCIM}, or {@link Environment#DIRECTORY_DOCUMENTS}.
     * @return File 如果已经挂载存储器，则返回路径，否则返回空。
     */
    @Nullable
    public static File getExternalStoragePublicDirectory(String type) {
        if (isExternalStorageMounted()) {
            return Environment.getExternalStoragePublicDirectory(type == null ? "" : type);
        }
        return null;
    }

    /**
     * 内置存储器 下载缓存目录
     * 下载完成后再将文件移动到目的目录
     *
     * @return File
     */
    @Nullable
    public static File getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 内置存储器 私有目录 缓存路径
     *
     * @param context Context
     * @return File
     */
    @Nullable
    public static File getExternalCacheDir(@NonNull Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 内置存储器 私有目录 文件路径
     *
     * @param context Context
     * @param type    存储目录类型，如果为空则没有对应类型的子目录。
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.
     * @return File
     */
    @Nullable
    public static File getExternalFileDir(@NonNull Context context, @Nullable String type) {
        return context.getExternalFilesDir(type);
    }

    /**
     * 内部存储 用户数据 缓存目录
     *
     * @param context Contexts
     * @return File
     */
    public static File getCacheDir(@NonNull Context context) {
        return context.getCacheDir();
    }

    /**
     * 内部存储 用户数据 文件目录
     *
     * @param context Contexts
     * @return File
     */
    public static File getFilesDir(@NonNull Context context) {
        return context.getFilesDir();
    }

}
