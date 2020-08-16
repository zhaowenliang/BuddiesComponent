package cc.buddies.component.storage.media.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 媒体实体类
 * Create by: chenWei.li
 * Date: 2018/8/22
 * Time: 上午12:36
 * Email: lichenwei.me@foxmail.com
 */
public class MediaFile implements Parcelable {

    private String path;
    private String mime;
    private Integer folderId;
    private String folderName;
    private Integer size;
    private long duration;
    private long dateToken;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.mime);
        dest.writeValue(this.folderId);
        dest.writeString(this.folderName);
        dest.writeValue(this.size);
        dest.writeLong(this.duration);
        dest.writeLong(this.dateToken);
    }

    public MediaFile() {
    }

    protected MediaFile(Parcel in) {
        this.path = in.readString();
        this.mime = in.readString();
        this.folderId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.folderName = in.readString();
        this.size = (Integer) in.readValue(Integer.class.getClassLoader());
        this.duration = in.readLong();
        this.dateToken = in.readLong();
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel source) {
            return new MediaFile(source);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };
}

