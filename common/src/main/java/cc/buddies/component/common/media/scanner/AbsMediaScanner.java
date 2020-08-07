package cc.buddies.component.common.media.scanner;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * 媒体库查询任务基类
 * Create by: chenWei.li
 * Date: 2019/1/21
 * Time: 8:35 PM
 * Email: lichenwei.me@foxmail.com
 */
public abstract class AbsMediaScanner<T> {

    private Context mContext;

    public AbsMediaScanner(Context context) {
        this.mContext = context;
    }

    /**
     * 查询URI
     *
     * @return Uri
     */
    protected abstract Uri getScanUri();

    /**
     * 查询列名
     *
     * @return String[]
     */
    protected abstract String[] getProjection();

    /**
     * 查询条件
     *
     * @return String
     */
    protected abstract String getSelection();

    /**
     * 查询条件值
     *
     * @return String[]
     */
    protected abstract String[] getSelectionArgs();

    /**
     * 查询排序
     *
     * @return String
     */
    protected abstract String getOrder();

    /**
     * 对外暴露游标，让开发者灵活构建对象
     *
     * @param cursor 查询游标
     * @return T
     */
    protected abstract T parse(Cursor cursor);

    /**
     * 根据查询条件进行媒体库查询，隐藏查询细节，让开发者更专注业务
     *
     * @return ArrayList<T>
     */
    public ArrayList<T> queryMedia() {
        ArrayList<T> list = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();

        try (Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder())) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T t = parse(cursor);
                    list.add(t);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
