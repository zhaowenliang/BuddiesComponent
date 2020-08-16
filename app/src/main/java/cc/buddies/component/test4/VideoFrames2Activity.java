package cc.buddies.component.test4;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.buddies.component.R;
import cc.buddies.component.storage.StorageUtils;
import cc.buddies.component.test4.adapter.VideoFramesAdapter;
import cc.buddies.component.utils.DimensionUtils;
import cc.buddies.component.videoeditor.VideoEditorUtils;
import cc.buddies.component.videoeditor.retriever.hardware.ExtractVideoFramesTask;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用MediaCodec硬解码解帧
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoFrames2Activity extends AppCompatActivity {

    private TextView mTextCount;
    private RecyclerView mRecyclerView;

    private VideoFramesAdapter mFramesAdapter;
    private List<Bitmap> mData = new ArrayList<>();

    private ExtractVideoFramesTask mExtractVideoFramesTask;

    private int mItemOffset;
    private int mItemWidth;
    private int mItemHeight;
    private int mSpanCount;

    private String mPath;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_video_frames);

        this.mPath = getIntent().getStringExtra("video_path");

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
        if (mPath == null) {
            File fileRootDir = StorageUtils.getExternalStoragePublicDirectory("测试视频编辑");
            if (fileRootDir != null) {
                // 1553241169643.mp4  video_origin.mp4  VID_20191105_140229.mp4  VID_20191106_191411.mp4  VID20191107105238.mp4
                File fileVideo = new File(fileRootDir, "VID_20191112_101104.mp4");
                this.mPath = fileVideo.getAbsolutePath();
            } else {
                this.mPath = "";
            }
        }

        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(mPath);
            int trackIndex = VideoEditorUtils.getExtractorMediaTrackIndex(extractor, "video/");
            MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);

            // 当前硬件解码器不能解码该视频
            if (!VideoEditorUtils.isSupportedDecoder(trackFormat)) {
                throw new Exception("not supported decoder!");
            }

            int videoWidth = trackFormat.containsKey(MediaFormat.KEY_WIDTH) ? trackFormat.getInteger(MediaFormat.KEY_WIDTH) : 0;
            int videoHeight = trackFormat.containsKey(MediaFormat.KEY_HEIGHT) ? trackFormat.getInteger(MediaFormat.KEY_HEIGHT) : 0;
            int videoRotation = VideoEditorUtils.getVideoRotation(trackFormat, mPath);

            boolean is90Degree = videoRotation % 180 != 0;
            this.mVideoWidth = is90Degree ? videoHeight : videoWidth;
            this.mVideoHeight = is90Degree ? videoWidth : videoHeight;

            this.mSpanCount = mVideoWidth > mVideoHeight ? 2 : 3;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            extractor.release();
        }

        // 如果获取视频信息异常，则默认设置列表为两列。
        mSpanCount = mSpanCount == 0 ? 2 : mSpanCount;
        mItemOffset = DimensionUtils.dp2px(this, 6);

        // 设置预览封面宽高
        float ratio = (mVideoWidth == 0) ? 0 : (float) mVideoHeight / mVideoWidth;
        mItemWidth = (getResources().getDisplayMetrics().widthPixels - mItemOffset * (mSpanCount + 1)) / mSpanCount;
        mItemHeight = (int) (ratio * mItemWidth);
    }

    private void retrieverFrames() {
        mExtractVideoFramesTask = new ExtractVideoFramesTask(this);
        mExtractVideoFramesTask.onlySyncFrame = true;

        // 结束/取消
        mExtractVideoFramesTask.onExtractFrameFinishListener = isCancelled -> Log.d("aaaa", "onExtractFrameFinish()  isCancelled: " + isCancelled);

        // 准备完成
        mExtractVideoFramesTask.onExtractFramePreparedListener = (codec, format, config) -> {
            // 大于60秒解析出60帧，小于60秒则每秒一帧。
            long duration = format.getLong(MediaFormat.KEY_DURATION);
            int totalFramesCount;
            long interval;

            int maxCount = 60;
            if (duration >= 60 * 1000 * 1000) {
                totalFramesCount = maxCount;
                interval = duration / (totalFramesCount - 1);
            } else {
                totalFramesCount = (int) (duration / 1000 / 1000) + 1;
                interval = 1000 * 1000;
            }

            config.maxCount = totalFramesCount;
            config.interval = interval;
            config.targetWidth = mItemWidth;
            config.targetHeight = mItemHeight;

            mTextCount.setText(MessageFormat.format("{0}/{1}", mFramesAdapter.getItemCount(), totalFramesCount));
            codec.start();
        };

        // 解帧结果
        mExtractVideoFramesTask.onExtractFrameResultListener = (totalCount, time, bitmap) -> runOnUiThread(() -> {
            mFramesAdapter.addItem(bitmap, time);
            mFramesAdapter.notifyItemInserted(mFramesAdapter.getItemCount() - 1);

            mTextCount.setText(MessageFormat.format("{0}/{1}", mFramesAdapter.getItemCount(), totalCount));
        });

        try {
            mExtractVideoFramesTask.setDataSource(mPath);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            mExtractVideoFramesTask.release();
            mExtractVideoFramesTask = null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExtractVideoFramesTask != null) {
            mExtractVideoFramesTask.release();
            mExtractVideoFramesTask = null;
        }

        if (mData != null) {
            for (Bitmap bitmap : mData)
                bitmap.recycle();
        }
    }

}
