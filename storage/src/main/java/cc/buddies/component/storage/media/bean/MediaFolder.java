package cc.buddies.component.storage.media.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 图片文件夹实体类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午12:56
 * Email: lichenwei.me@foxmail.com
 */
public class MediaFolder implements Parcelable {

    private int folderId;
    private String folderName;
    private String folderCover;
    private boolean isCheck;
    private ArrayList<MediaFile> mediaFileList;

    public MediaFolder(int folderId, String folderName, String folderCover, ArrayList<MediaFile> mediaFileList) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderCover = folderCover;
        this.mediaFileList = mediaFileList;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCover() {
        return folderCover;
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ArrayList<MediaFile> getMediaFileList() {
        return mediaFileList;
    }

    public void setMediaFileList(ArrayList<MediaFile> mediaFileList) {
        this.mediaFileList = mediaFileList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.folderId);
        dest.writeString(this.folderName);
        dest.writeString(this.folderCover);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mediaFileList);
    }

    protected MediaFolder(Parcel in) {
        this.folderId = in.readInt();
        this.folderName = in.readString();
        this.folderCover = in.readString();
        this.isCheck = in.readByte() != 0;
        this.mediaFileList = in.createTypedArrayList(MediaFile.CREATOR);
    }

    public static final Creator<MediaFolder> CREATOR = new Creator<MediaFolder>() {
        @Override
        public MediaFolder createFromParcel(Parcel source) {
            return new MediaFolder(source);
        }

        @Override
        public MediaFolder[] newArray(int size) {
            return new MediaFolder[size];
        }
    };
}
