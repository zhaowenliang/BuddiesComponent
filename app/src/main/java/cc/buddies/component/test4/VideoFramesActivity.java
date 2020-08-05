package cc.buddies.component.test4;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.buddies.component.R;
import cc.buddies.component.storage.StorageUtils;
import cc.buddies.component.test4.adapter.VideoFramesAdapter;
import cc.buddies.component.utils.DimensionUtils;
import cc.buddies.component.videoeditor.retriever.software.RetrieverFramesAsyncTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用MediaMetadataRetriever软解码解帧
 */
public class VideoFramesActivity extends AppCompatActivity {

    private TextView mTextCount;
    private RecyclerView mRecyclerView;
    private VideoFramesAdapter mFramesAdapter;
    private List<Bitmap> mData = new ArrayList<>();
    private RetrieverFramesAsyncTask mAsyncTask;
    private RetrieverFramesTask retrieverFrames;
    private int totalFramesCount;

    private int mItemOffset;
    private int mItemWidth;
    private int mItemHeight;
    private int mSpanCount;

    private String mPath;
    private int mVideoWidth;
    private int mVideoHeight;
    private long mVideoDuration;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_video_frames);

        initVideoInfo();
        initView();
        retrieverFrames();
    }

    private void initView() {
        mTextCount = findViewById(R.id.tv_count);

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            long checkedTime = mFramesAdapter != null ? mFramesAdapter.getCheckedTime() : -1L;
            Toast.makeText(v.getContext(), "选择时间点: " + checkedTime, Toast.LENGTH_SHORT).show();
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mSpanCount));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (!(parent.getLayoutManager() instanceof GridLayoutManager)) return;

                int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
                int position = parent.getChildAdapterPosition(view);
                int column = position % spanCount; // item column

                outRect.left = mItemOffset - column * mItemOffset / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * mItemOffset / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = mItemOffset;
                }
                outRect.bottom = mItemOffset; // item bottom
            }
        });

        if (mFramesAdapter == null) {
            mFramesAdapter = new VideoFramesAdapter(mData);
            mFramesAdapter.setWidth(mItemWidth);
            mFramesAdapter.setHeight(mItemHeight);
            mRecyclerView.setAdapter(mFramesAdapter);
        }
    }

    private void initVideoInfo() {
        File fileRootDir = StorageUtils.getExternalStoragePublicDirectory("测试视频编辑");
        if (fileRootDir != null) {
            // 1553241169643.mp4  video_origin.mp4  VID_20191105_140229.mp4
            File fileVideo = FileUtils.getFile(fileRootDir, "VID_20191106_191411.mp4");
            this.mPath = fileVideo.getAbsolutePath();
        } else {
            this.mPath = "";
        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mPath);

        String strWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String strHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String strDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String strRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        retriever.release();

        // 转化旋转角度类型
        int rotation = 0;
        try {
            rotation = Integer.parseInt(strRotation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mItemOffset = DimensionUtils.dp2px(this, 6);

        try {
            boolean is90Degree = rotation % 180 != 0;
            mVideoWidth = Integer.parseInt(is90Degree ? strHeight : strWidth);
            mVideoHeight = Integer.parseInt(is90Degree ? strWidth : strHeight);
            mVideoDuration = Long.parseLong(strDuration);
            float ratio = (float) mVideoHeight / mVideoWidth;
            mSpanCount = mVideoWidth > mVideoHeight ? 2 : 3;
            mItemWidth = (getResources().getDisplayMetrics().widthPixels - mItemOffset * (mSpanCount + 1)) / mSpanCount;
            mItemHeight = (int) (ratio * mItemWidth);
        } catch (Exception ignore) {
            // 如果获取视频信息异常，则默认设置列表为两列。
            mSpanCount = mSpanCount == 0 ? 2 : mSpanCount;
        }
    }

    private void retrieverFrames() {
        // 大于60秒解析出60帧，小于60秒则每秒一帧。
        long interval;
        if (mVideoDuration >= 60 * 1000) {
            totalFramesCount = 60;
            interval = mVideoDuration * 1000 / (totalFramesCount - 1);
        } else {
            totalFramesCount = (int) (mVideoDuration / 1000) + 1;
            interval = 1000 * 1000;
        }

        retrieverFrames = new RetrieverFramesTask();
        mAsyncTask = new RetrieverFramesAsyncTask(retrieverFrames, mPath);
        mAsyncTask.setOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        mAsyncTask.setRect(mItemWidth, mItemHeight);
        mAsyncTask.execute(interval);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        if (retrieverFrames != null) {
            retrieverFrames = null;
        }
        if (mData != null) {
            for (Bitmap bitmap : mData) {
                bitmap.recycle();
            }
        }
    }

    private class RetrieverFramesTask implements RetrieverFramesAsyncTask.RetrieverFramesCallback {

        @Override
        public void onPreExecute() {
            mTextCount.setText(MessageFormat.format("{0}/{1}", 0, totalFramesCount));
        }

        @Override
        public void onUpdate(@NonNull final Bitmap bitmap, final long time) {
            mFramesAdapter.addItem(bitmap, time);

            int itemCount = mFramesAdapter.getItemCount();
            mFramesAdapter.notifyItemInserted(itemCount - 1);

            mTextCount.setText(MessageFormat.format("{0}/{1}", itemCount, totalFramesCount));
        }

        @Override
        public void onCompleted(final int count) {
            Toast.makeText(VideoFramesActivity.this, "解析帧完成", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled() {
            Toast.makeText(VideoFramesActivity.this, "解析帧任务取消", Toast.LENGTH_SHORT).show();
        }
    }

}
