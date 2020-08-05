package cc.buddies.component.storage.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.lang.ref.WeakReference;

import cc.buddies.component.storage.media.scanner.AbsMediaScanner;

/**
 * 使用Loader的方式异步加载数据（参考使用），
 * 也可以使用{@link AbsMediaScanner}来加载媒体数据。
 * <pre>
 *     Loader工具来自于androidx.loader:loader:x.x.x
 *
 *     使用LoaderManager加载Loader，在相同的activity/fragment中相同loaderId的Loader可复用。
 *     Cursor有对源数据的实时观察，如果源数据变化，则会实时更新数据回调。
 *     也可以继承AsyncTaskLoader<T>重写异步加载数据。
 *
 *     eg：
 *     int loaderId = 0;
 *     Bundle bundle = new Bundle();
 *     LoaderCallback loaderCallback = new LoaderCallback(this, mAdapter);
 *     LoaderManager.getInstance(this).initLoader(loaderId, bundle, loaderCallback);
 * </pre>
 */
public abstract class CursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    private WeakReference<Context> mContextWeakReference;
    private LoaderCallbacks mLoaderCallbacks;

    public CursorLoaderCallback(Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    public void setLoaderCallbacks(final LoaderCallbacks loaderCallbacks) {
        this.mLoaderCallbacks = loaderCallbacks;
    }

    // 绑定加载器
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(final int id, @Nullable final Bundle args) {
        Uri scanUri = getScanUri();
        String[] projection = getProjection();
        String selection = getSelection();
        String[] selectionArgs = getSelectionArgs();
        String order = getOrder();

        return new CursorLoader(mContextWeakReference.get(), scanUri, projection, selection, selectionArgs, order);
    }

    // 加载到数据
    @Override
    public void onLoadFinished(@NonNull final Loader<Cursor> loader, final Cursor data) {
        if (mLoaderCallbacks != null) {
            mLoaderCallbacks.onLoadFinished(loader, data);
        }
    }

    // 重新加载
    @Override
    public void onLoaderReset(@NonNull final Loader<Cursor> loader) {
        if (mLoaderCallbacks != null) {
            mLoaderCallbacks.onLoaderReset(loader);
        }
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
     * 处理回调接口
     */
    public interface LoaderCallbacks {
        void onLoadFinished(@NonNull final Loader<Cursor> loader, final Cursor data);

        void onLoaderReset(@NonNull final Loader<Cursor> loader);
    }

}
