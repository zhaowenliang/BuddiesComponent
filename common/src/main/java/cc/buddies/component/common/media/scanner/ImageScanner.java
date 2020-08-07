package cc.buddies.component.common.media.scanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import cc.buddies.component.common.media.bean.MediaFile;

/**
 * 媒体库扫描类(图片)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class ImageScanner extends AbsMediaScanner<MediaFile> {

    public ImageScanner(Context context) {
        super(context);
    }

    @Override
    protected Uri getScanUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };
    }

    @Override
    protected String getSelection() {
        return MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?" + " or " + MediaStore.Images.Media.MIME_TYPE + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[]{"image/jpeg", "image/png", "image/gif"};
    }

    @Override
    protected String getOrder() {
        return MediaStore.Images.Media.DATE_TAKEN + " desc";
    }

    /**
     * 构建媒体对象
     *
     * @param cursor 查询游标
     * @return MediaFile
     */
    @Override
    protected MediaFile parse(Cursor cursor) {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setPath(path);
        mediaFile.setMime(mime);
        mediaFile.setFolderId(folderId);
        mediaFile.setFolderName(folderName);
        mediaFile.setDateToken(dateToken);

        return mediaFile;
    }

}
