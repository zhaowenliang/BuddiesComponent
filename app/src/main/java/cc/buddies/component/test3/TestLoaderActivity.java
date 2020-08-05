package cc.buddies.component.test3;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.buddies.component.R;
import cc.buddies.component.test3.adapter.CursorAdapter;
import cc.buddies.component.test3.adapter.TestLoaderAdapter;

import java.lang.ref.WeakReference;


public class TestLoaderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestLoaderAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_loader_list);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new TestLoaderAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        int loaderId = 0;
        Bundle bundle = new Bundle();
        LoaderCallback loaderCallback = new LoaderCallback(this, mAdapter);
        LoaderManager.getInstance(this).initLoader(loaderId, bundle, loaderCallback);
    }

    // LoaderManager 将自动管理加载器的生命周期。(Lifecycle)
    private static class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContextWeakReference;
        private CursorAdapter mCursorAdapter;

        public LoaderCallback(Context context, CursorAdapter cursorAdapter) {
            this.mContextWeakReference = new WeakReference<>(context);
            this.mCursorAdapter = cursorAdapter;
        }

        // 绑定加载器
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(final int id, @Nullable final Bundle args) {
            Uri contentUri = ContactsContract.Contacts.CONTENT_URI;
            return new CursorLoader(mContextWeakReference.get(), contentUri, null, null, null, null);
        }

        // 加载到数据
        @Override
        public void onLoadFinished(@NonNull final Loader<Cursor> loader, final Cursor data) {
            // CursorAdapter 继承自 ListView Adapter
            // RecyclerView Adapter 需要单独实现 Cursor
            if (mCursorAdapter != null) {
                mCursorAdapter.swapCursor(data);
            }
        }

        // 重新加载
        @Override
        public void onLoaderReset(@NonNull final Loader<Cursor> loader) {
            if (mCursorAdapter != null) {
                mCursorAdapter.swapCursor(null);
            }
        }
    }

}
