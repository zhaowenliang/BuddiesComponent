package cc.buddies.component.storage.media.scanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import cc.buddies.component.storage.media.bean.MediaFile;

/**
 * 媒体库扫描类(视频)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class VideoScanner extends AbsMediaScanner<MediaFile> {

    private int maxSize;
    private long maxDuration;

    public VideoScanner(Context context, int maxSize, long maxDuration) {
        super(context);
        this.maxSize = maxSize;
        this.maxDuration = maxDuration;
    }

    @Override
    protected Uri getScanUri() {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.MediaColumns.SIZE
        };
    }

    @Override
    protected String getSelection() {
        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(MediaStore.MediaColumns.SIZE).append(" > 0")
                .append(" and ")
                .append(MediaStore.Video.Media.DURATION).append(" > 0 ");

        if (maxSize > 0) {
            selectionBuilder.append(" and ");
            selectionBuilder.append(MediaStore.MediaColumns.SIZE).append(" <= ? ");
        }

        if (maxDuration > 0) {
            selectionBuilder.append(" and ");
            selectionBuilder.append(MediaStore.Video.Media.DURATION).append(" <= ? ");
        }
        return selectionBuilder.toString();
    }

    @Override
    protected String[] getSelectionArgs() {
        if (maxSize > 0 && maxDuration > 0) {
            return new String[]{String.valueOf(maxSize), String.valueOf(maxDuration)};
        }

        if (maxSize > 0) {
            return new String[]{String.valueOf(maxSize)};
        }

        if (maxDuration > 0) {
            return new String[]{String.valueOf(maxDuration)};
        }

        return null;
    }

    @Override
    protected String getOrder() {
        return MediaStore.Video.Media.DATE_TAKEN + " desc";
    }

    /**
     * 构建媒体对象
     *
     * @param cursor 查询游标
     * @return MediaFile
     */
    @Override
    protected MediaFile parse(Cursor cursor) {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
        Integer size = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setPath(path);
        mediaFile.setMime(mime);
        mediaFile.setFolderId(folderId);
        mediaFile.setFolderName(folderName);
        mediaFile.setSize(size);
        mediaFile.setDuration(duration);
        mediaFile.setDateToken(dateToken);

        return mediaFile;
    }

}
