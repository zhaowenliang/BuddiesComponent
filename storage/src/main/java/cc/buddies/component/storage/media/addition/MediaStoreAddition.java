package cc.buddies.component.storage.media.addition;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 媒体文件添加到媒体数据库
 * <p>
 * 如果存储到
 * {@link android.os.Environment#DIRECTORY_DCIM},
 * {@link android.os.Environment#DIRECTORY_PICTURES},
 * {@link android.os.Environment#DIRECTORY_MOVIES},
 * {@link android.os.Environment#DIRECTORY_MUSIC}
 * 目录则系统媒体扫描程序会自动将其加入媒体数据库。
 * </p>
 * <pre>eg:
 *     File file = new File(StorageUtils.getExternalStorageDirectory(), "image.jpg");
 *     String path = file.getAbsolutePath();
 *     String mime = "image/jpeg";
 *
 *     ContentValues contentValues = MediaStoreAddition.getImageContentValues(path, mime);
 *     MediaStoreAddition.insert(getContentResolver(), contentValues, MediaStoreAddition.MEDIA_STORE_IMAGES_MEDIA_EXTERNAL_CONTENT_URI);
 * </pre>
 */
public class MediaStoreAddition {

    private static final String TAG = "MediaStoreAddition";

    // 图片存储Uri地址
    public static final Uri MEDIA_STORE_IMAGES_MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri MEDIA_STORE_IMAGES_MEDIA_INTERNAL_CONTENT_URI = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

    // 视频存储Uri地址
    public static final Uri MEDIA_STORE_VIDEO_MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final Uri MEDIA_STORE_VIDEO_MEDIA_INTERNAL_CONTENT_URI = MediaStore.Video.Media.INTERNAL_CONTENT_URI;

    public static ContentValues getImageContentValues(String path, String mime) {
        ContentValues values = new ContentValues();

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

            int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            double latitude = exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, 0);
            double longitude = exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, 0);

            String strDate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            long datetime = strDate == null ? System.currentTimeMillis() : 0;
            try {
                if (strDate != null) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    Date parse = dateFormat.parse(strDate);
                    if (parse != null) {
                        datetime = parse.getTime();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            File file = new File(path);
            long size = file.length();

            return getImageContentValues(path, null, mime, width, height, degree, size, datetime, location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }

    /**
     * 获取存储Image的ContentValues
     *
     * @param path        文件路径
     * @param title       标题
     * @param mime        文件扩展类型
     * @param width       宽度
     * @param height      高度
     * @param orientation 角度 Only degrees 0, 90, 180, 270 will work.
     * @param size        字节大小
     * @param date        时间戳（毫秒）
     * @param location    位置
     * @return ContentValues
     */
    public static ContentValues getImageContentValues(String path, String title, String mime,
                                                      int width, int height, int orientation,
                                                      long size, long date, Location location) {
        ContentValues values = new ContentValues(11);

        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, FilenameUtils.getName(path));
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mime);
        values.put(MediaStore.Images.ImageColumns.TITLE, title);
        values.put(MediaStore.Images.ImageColumns.DATA, path);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        values.put(MediaStore.Images.ImageColumns.SIZE, size);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation);

        if (location != null) {
            values.put(MediaStore.Images.ImageColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Images.ImageColumns.LONGITUDE, location.getLongitude());
        }
        return values;
    }

    public static ContentValues getVideoContentValues(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        String strWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String strHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String strDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String strMimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String strDate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String strLocation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);

        Location location = new Location("");
        try {
            // "-90.0000+180.0000"
            int longitudeIndex = strLocation.indexOf("-");
            int latitudeIndex = strLocation.indexOf("+");

            String strLongitude = strLocation.substring(longitudeIndex + 1, latitudeIndex);
            String strLatitude = strLocation.substring(latitudeIndex + 1);

            location.setLongitude(Double.parseDouble(strLongitude));
            location.setLatitude(Double.parseDouble(strLatitude));
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(path);
        long size = file.length();

        int width = 0;
        int height = 0;
        long duration = 0;
        long datetime = 0;
        try {
            width = Integer.parseInt(strWidth);
            height = Integer.parseInt(strHeight);
            duration = Long.parseLong(strDuration);

            DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
            Date parse = dateFormat.parse(strDate);
            if (parse != null) {
                datetime = parse.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        retriever.release();

        return getVideoContentValues(path, null, strMimeType, width, height, size, duration, datetime, location);
    }

    /**
     * 获取存储Video的ContentValues
     *
     * @param path     文件路径
     * @param title    标题
     * @param mime     文件扩展类型
     * @param width    宽度
     * @param height   高度
     * @param size     字节大小
     * @param duration 时长（毫秒）
     * @param date     时间戳（毫秒）
     * @param location 位置
     * @return ContentValues
     */
    public static ContentValues getVideoContentValues(String path, String title, String mime,
                                                      int width, int height,
                                                      long size, long duration, long date, Location location) {
        ContentValues values = new ContentValues(11);

        values.put(MediaStore.Video.VideoColumns.DISPLAY_NAME, FilenameUtils.getName(path));
        values.put(MediaStore.Video.VideoColumns.MIME_TYPE, mime);
        values.put(MediaStore.Video.VideoColumns.TITLE, title);
        values.put(MediaStore.Video.VideoColumns.DATA, path);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        values.put(MediaStore.Video.VideoColumns.SIZE, size);
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, date);
        values.put(MediaStore.Video.VideoColumns.DURATION, duration);

        if (location != null) {
            values.put(MediaStore.Video.VideoColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Video.VideoColumns.LONGITUDE, location.getLongitude());
        }
        return values;
    }


    /**
     * 将媒体数据插入MediaStore
     *
     * @param resolver  ContentResolver
     * @param values    插入数据
     * @param targetUri 插入地址
     *                  {@link android.provider.MediaStore.Images.Media#INTERNAL_CONTENT_URI}
     *                  {@link android.provider.MediaStore.Images.Media#EXTERNAL_CONTENT_URI}
     *                  {@link android.provider.MediaStore.Video.Media#INTERNAL_CONTENT_URI}
     *                  {@link android.provider.MediaStore.Video.Media#EXTERNAL_CONTENT_URI}
     * @return Uri
     */
    public static Uri insert(ContentResolver resolver, ContentValues values, Uri targetUri) {
        Uri uri = null;
        try {
            uri = resolver.insert(targetUri, values);
        } catch (Throwable throwable) {
            Log.e(TAG, "Failed to write MediaStore:" + throwable);
        }
        return uri;
    }
}
