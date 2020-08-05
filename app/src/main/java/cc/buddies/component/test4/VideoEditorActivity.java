package cc.buddies.component.test4;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cc.buddies.component.R;
import cc.buddies.component.storage.StorageUtils;
import cc.buddies.component.test4.adapter.VideoEditorFramesAdapter;
import cc.buddies.component.test4.view.VideoFrameRecyclerView;
import cc.buddies.component.test4.view.VideoTimeSelectView;
import cc.buddies.component.videoeditor.Utils;
import cc.buddies.component.videoeditor.cut.VideoCutAsyncTask;
import cc.buddies.component.videoeditor.retriever.hardware.ExtractVideoFrameConfig;
import cc.buddies.component.videoeditor.retriever.hardware.ExtractVideoFramesTask;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameFinishListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFramePreparedListener;
import cc.buddies.component.videoeditor.retriever.hardware.listener.OnExtractFrameResultListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 视频编辑页面
 * <p>包含视频起止时间选择裁剪时间、视频旋转、视频压缩</p>
 * <pre>
 *     页面初始化流程：
 *     1. 先初始化TextureView视频渲染画布，然后初始化视频播放器，播放器准备好后，播放视频 和 解析视频预览帧。
 *     2. 视频 起止时间选择滑块滑动 和 帧列表滑动，都会影响视频起止时间的选择。
 *     3. 视频时长选择有 最大时间 和 最小时间 限制。
 *     4. 视频 起止时间点 和 旋转角度 确定后，统一进行视频裁剪/旋转/压缩。
 * </pre>
 */
public class VideoEditorActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener,
        VideoTimeSelectView.OnSeekBarListener, VideoTimeSelectView.OnProgressBarListener,
        OnExtractFramePreparedListener, OnExtractFrameResultListener, OnExtractFrameFinishListener {

    // 默认最大选择时间
    private long DEFAULT_MAX_TIME = 60 * 1000 * 1000;
    // 默认最小选择时间
    private long DEFAULT_MIN_TIME = 1000 * 1000;

    private VideoTimeSelectView mTimeSelectView;
    private TextView mSelectDurationText;

    private VideoFrameRecyclerView mFrameRecyclerView;
    private VideoEditorFramesAdapter mEditorFramesAdapter;
    private ExtractVideoFramesTask mExtractVideoFramesTask;

    // 视频裁剪任务
    private VideoCutAsyncTask mVideoCutAsyncTask;

    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private Surface mSurface;

    private String mVideoPath;
    private int mVideoSizeWidth;
    private int mVideoSizeHeight;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    private long mVideoDuration;

    // 手动操作旋转角度
    private int mDegrees;

    // 播放器是否已经准备好播放
    private boolean isPlayerPrepared = false;

    // 选择框最大选择时间
    private long mSelectMaxTime;

    // 起始时间选择滑块 偏移时间量
    private long mStartSelectSeekOffsetTime;

    // 结束时间选择滑块 相对 起始时间选择滑块 偏移时间量
    private long mEndSelectSeekOffsetTime;

    // 帧列表滚动 偏移时间量
    private long mFrameListOffsetTime;

    // 总预览帧数
    private int mTotalFramesCount;
    // 帧列表总长度
    private long mFrameListTotalLength;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_video_editor);

        initView();
        initVideo();
        initFrameList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextureView.setKeepScreenOn(true);
        if (isPlayerPrepared) {
            playerStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTextureView.setKeepScreenOn(false);
        playerPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消视频裁剪任务
        if (mVideoCutAsyncTask != null) {
            mVideoCutAsyncTask.cancel(true);
            mVideoCutAsyncTask = null;
        }

        // 销毁视频帧解析器
        if (mExtractVideoFramesTask != null) {
            mExtractVideoFramesTask.release();
            mExtractVideoFramesTask = null;
        }

        // 销毁播放器
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            isPlayerPrepared = false;
        }
    }

    private void showProgressDialog(int progress) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("视频处理中...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(0);

            mProgressDialog.setOnCancelListener(dialog -> {
                if (mVideoCutAsyncTask != null) {
                    mVideoCutAsyncTask.cancel(true);
                    mVideoCutAsyncTask = null;
                }
            });
        }

        mProgressDialog.setProgress(progress);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initView() {
        mFrameRecyclerView = findViewById(R.id.recycler_view);
        mTextureView = findViewById(R.id.texture_view);
        mTimeSelectView = findViewById(R.id.time_select_view);
        mSelectDurationText = findViewById(R.id.tv_select_duration);

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        // 点击旋转
        findViewById(R.id.btn_rotate).setOnClickListener(v -> {
            int viewWidth = mTextureView.getWidth();
            int viewHeight = mTextureView.getHeight();
            mDegrees += 90;

            Matrix matrix = Utils.getTextureViewSizeCenterMatrix(mDegrees, viewWidth, viewHeight, mVideoSizeWidth, mVideoSizeHeight);
            mTextureView.setTransform(matrix);
        });

        // 点击下一步
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            long clipTime = mFrameListOffsetTime + mStartSelectSeekOffsetTime;
            long clipDuration = mEndSelectSeekOffsetTime - mStartSelectSeekOffsetTime;

            File fileRootDir = StorageUtils.getExternalStoragePublicDirectory("测试视频编辑");
            if (fileRootDir == null) {
                Toast.makeText(v.getContext(), "获取不到视频输出路径", Toast.LENGTH_SHORT).show();
                return;
            }

            File clipDir = FileUtils.getFile(fileRootDir, "clip");
            if (!cc.buddies.component.storage.io.FileUtils.createDir(clipDir)) {
                Toast.makeText(v.getContext(), "创建视频输出路径失败", Toast.LENGTH_SHORT).show();
                return;
            }

            File outFile = new File(clipDir, "VIDEO_" + System.currentTimeMillis() + ".mp4");

            // 由基准边计算宽高缩放比例(视频旋转校正后，宽不能超过1080)
            int baseSide = this.mVideoRotation % 180 == 90 ? this.mVideoHeight : this.mVideoWidth;
            float scale = baseSide <= 1080 ? 1F : 1080F / baseSide;
            int outWidth = (int) (scale * this.mVideoWidth);
            int outHeight = (int) (scale * this.mVideoHeight);

            mVideoCutAsyncTask = new VideoCutAsyncTask();
            mVideoCutAsyncTask.setOnVideoCutCallback(new VideoCutAsyncTask.OnVideoCutCallback() {
                @Override
                public void onStart() {
                    showProgressDialog(0);
                }

                @Override
                public void onProgress(final float progress) {
                    showProgressDialog((int) (progress * 100));
                }

                @Override
                public void onSuccess(@NonNull final String output) {
                    dismissProgressDialog();
                }

                @Override
                public void onError(@NonNull final Exception e) {
                    dismissProgressDialog();
                }

                @Override
                public void onCancel(@NonNull final String output) {
                    dismissProgressDialog();
                }
            });

            mVideoCutAsyncTask.execute(new VideoCutAsyncTask.Processor()
                    .input(this.mVideoPath)
                    .output(outFile.getAbsolutePath())
                    .outWidth(outWidth)
                    .outHeight(outHeight)
                    .degrees(this.mDegrees % 360)
                    .startTimeMs((int) (clipTime / 1000))
                    .endTimeMs((int) ((clipTime + clipDuration) / 1000))
                    .bitrate(4992 * 1000)
                    .frameRate(24));
        });

        mTextureView.setSurfaceTextureListener(this);
        mTimeSelectView.setOnSeekBarListener(this);
        mTimeSelectView.setOnProgressBarListener(this);
    }

    private void initVideo() {
        File fileRootDir = StorageUtils.getExternalStoragePublicDirectory("测试视频编辑");
        if (fileRootDir == null) return;

        // 1553241169643.mp4  video_origin.mp4  VID_20191109_141525.mp4  VID_20191112_101104.mp4
        // video_20191118_113559.mp4
        File fileVideo = FileUtils.getFile(fileRootDir, "VID_20191112_101104.mp4");
        this.mVideoPath = fileVideo.getAbsolutePath();

        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(this.mVideoPath);

            int trackIndex = Utils.getExtractorMediaTrackIndex(extractor, "video/");
            extractor.selectTrack(trackIndex);
            MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);

            // 当前硬件解码器不能解码该视频
            if (!Utils.isSupportedDecoder(trackFormat)) {
                throw new Exception("not supported decoder!");
            }

            this.mVideoDuration = trackFormat.containsKey(MediaFormat.KEY_DURATION) ? trackFormat.getLong(MediaFormat.KEY_DURATION) : 0;
            this.mVideoWidth = trackFormat.containsKey(MediaFormat.KEY_WIDTH) ? trackFormat.getInteger(MediaFormat.KEY_WIDTH) : 0;
            this.mVideoHeight = trackFormat.containsKey(MediaFormat.KEY_HEIGHT) ? trackFormat.getInteger(MediaFormat.KEY_HEIGHT) : 0;
            this.mVideoRotation = Utils.getVideoRotation(trackFormat, this.mVideoPath);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } finally {
            extractor.release();
        }

        if (mVideoDuration < DEFAULT_MAX_TIME) {
            mSelectMaxTime = mVideoDuration;
        } else {
            mSelectMaxTime = DEFAULT_MAX_TIME;
        }

        mTimeSelectView.setSelectMaxTime(mSelectMaxTime);
        mTimeSelectView.setSelectMinTime(DEFAULT_MIN_TIME);

        this.mStartSelectSeekOffsetTime = 0;
        this.mEndSelectSeekOffsetTime = this.mStartSelectSeekOffsetTime + mSelectMaxTime;
    }

    private void initFrameList() {
        if (mEditorFramesAdapter == null) {
            List<Bitmap> bitmaps = new ArrayList<>();
            this.mEditorFramesAdapter = new VideoEditorFramesAdapter(bitmaps);

            this.mFrameRecyclerView.setAdapter(this.mEditorFramesAdapter);
            this.mFrameRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

            int leftSpace = 0, rightSpace = 0;
            ViewGroup.LayoutParams layoutParams = mTimeSelectView.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                leftSpace = marginLayoutParams.leftMargin;
                rightSpace = marginLayoutParams.rightMargin;
            }

            // 两侧滑块固定宽为18dp
            int seekBarWidth = mTimeSelectView.getSeekBarWidth();
            final int startSpace = leftSpace + seekBarWidth;
            final int endSpace = rightSpace + seekBarWidth;

            this.mFrameRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent, @NonNull final RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    // 设置头部和尾部空隙
                    int position = parent.getChildAdapterPosition(view);
                    outRect.left = position == 0 ? startSpace : 0;
                    outRect.right = position == (mTotalFramesCount - 1) ? endSpace : 0;
                }
            });
        }

        // 帧列表滚动监听
        mFrameRecyclerView.setOnSlideListener(new VideoFrameRecyclerView.OnSlideListener() {
            // 帧列表偏移距离
            private int mFrameListOffset = 0;

            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                if (dx == 0) return;
                mFrameListOffset += dx;

                long startSeekOffsetTime = mStartSelectSeekOffsetTime;
                long listOffsetTime = mVideoDuration * mFrameListOffset / mFrameListTotalLength;
                long progressOffsetTime = (long) (mTimeSelectView.getTotalTime() * mTimeSelectView.getProgressPercent());

                mFrameListOffsetTime = listOffsetTime;
                int newTime = (int) ((startSeekOffsetTime + listOffsetTime + progressOffsetTime) / 1000);

                // 定位视频播放位置(毫秒)
                playerSeekTo(newTime);
            }

            @Override
            public void onTouchDown() {
                playerPause();
            }

            @Override
            public void onTouchUp() {
                playerStart();
            }
        });
    }

    // 初始化播放器
    private void initPlayer() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mVideoPath);
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mMediaPlayer.setOnPreparedListener(new PlayerOnPreparedListener());
                mMediaPlayer.setOnCompletionListener(new PlayerOnCompletionListener());
                mMediaPlayer.setOnVideoSizeChangedListener(new PlayerOnVideoSizeChangedListener());

                mMediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playerStart() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }

        if (mTimeSelectView != null) {
            mTimeSelectView.onProgressStart();
        }
    }

    private void playerPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }

        if (mTimeSelectView != null) {
            mTimeSelectView.onProgressPause();
        }
    }

    private void playerSeekTo(int msec) {
        if (mMediaPlayer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo(msec, MediaPlayer.SEEK_CLOSEST);
            } else {
                mMediaPlayer.seekTo(msec);
            }
        }
    }

    // 初始化解析视频帧任务
    private void initExtractFramesTask() {
        if (mExtractVideoFramesTask == null) {
            mExtractVideoFramesTask = new ExtractVideoFramesTask(this);
            mExtractVideoFramesTask.onExtractFramePreparedListener = this;
            mExtractVideoFramesTask.onExtractFrameResultListener = this;
            mExtractVideoFramesTask.onExtractFrameFinishListener = this;
            mExtractVideoFramesTask.onlySyncFrame = true;

            try {
                mExtractVideoFramesTask.setDataSource(mVideoPath);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                mExtractVideoFramesTask.release();
                mExtractVideoFramesTask = null;
            }
        }
    }

    //------------------------------------视频帧解析回调---------------------------------------------

    // 解析视频帧前准备
    @Override
    public void onExtractFramePrepared(@NonNull final MediaCodec codec, @NonNull final MediaFormat format, @NonNull final ExtractVideoFrameConfig config) {
        // 小于60秒，以取10帧，计算每帧时间间隔。
        // 大于60秒，根据视频总时长及第一个60秒大概每6秒一帧计算时间间隔。
        long duration = format.getLong(MediaFormat.KEY_DURATION);
        long interval;
        int totalFramesCount;
        long totalListLength;

        if (duration <= DEFAULT_MAX_TIME) {
            totalFramesCount = 10;
            interval = duration / (totalFramesCount - 1);
            totalListLength = mTimeSelectView.getSelectedMaxWidth();
        } else {
            // 根据头60s有10帧，计算大概帧间隔时长。
            int aboutInterval = (int) (DEFAULT_MAX_TIME / (10 - 1));

            // 以总时长为主，结合大概间隔，计算出最终间隔时长。目的是最后一个帧输出后，不留时间余数。
            totalFramesCount = (int) (duration / aboutInterval);
            interval = duration / totalFramesCount;

            // 60s 占 总时长 百分比 == 60s区域 占 帧列表总长度 百分比
            // DEFAULT_MAX_TIME / duration == mTimeSelectView.getSelectedMaxWidth() / x;
            // 帧列表总长度
            totalListLength = duration * mTimeSelectView.getSelectedMaxWidth() / DEFAULT_MAX_TIME;
        }

        int frameWidth = (int) (totalListLength / totalFramesCount);
        this.mEditorFramesAdapter.setItemWidth(frameWidth);
        this.mTotalFramesCount = totalFramesCount;

        // 如果超过最大可选择时长的视频，重新计算帧列表总长度，是为了消除计算帧宽度所产生的误差。
        if (duration <= DEFAULT_MAX_TIME) {
            this.mFrameListTotalLength = totalListLength;
        } else {
            this.mFrameListTotalLength = frameWidth * totalFramesCount;
        }

        config.startTime = 0;
        config.endTime = duration;
        config.maxCount = totalFramesCount;
        config.interval = interval;
        config.targetWidth = frameWidth;
        config.targetHeight = mFrameRecyclerView.getMeasuredHeight();

        Log.d("aaaa", "解析视频帧前准备: " + "  duration: " + duration + "  interval: " + interval + "  count: " + totalFramesCount);
        codec.start();
    }

    // 解析视频帧结果
    @Override
    public void onExtractFrameResult(final int totalCount, final long time, final Bitmap bitmap) {
        runOnUiThread(() -> {
            if (mEditorFramesAdapter != null) {
                mEditorFramesAdapter.addItem(bitmap);
            }
        });
    }

    // 解析视频帧结束
    @Override
    public void onExtractFrameFinish(final boolean isCancelled) {
        Log.d("aaaa", "解析视频帧结束 isCancelled: " + isCancelled);
        if (mExtractVideoFramesTask != null) {
            mExtractVideoFramesTask.release();
            mExtractVideoFramesTask = null;
        }
    }

    //------------------------------------时间选择控件回调-------------------------------------------

    // 起始滑块按下
    @Override
    public void onStartSeekBarTouch() {
        playerPause();
    }

    // 起始滑块抬起
    @Override
    public void onStartSeekBarRelease() {
        playerStart();
    }

    // 起始滑块滑动
    @Override
    public void onStartSeekBarSlide(final long offsetTime) {
        this.mStartSelectSeekOffsetTime = offsetTime;

        // 更新视频指向时间点
        long startTime = this.mFrameListOffsetTime + this.mStartSelectSeekOffsetTime;
        playerSeekTo((int) (startTime / 1000));
    }

    // 结束滑块按下
    @Override
    public void onEndSeekBarTouch() {
        playerPause();
    }

    // 结束滑块抬起
    @Override
    public void onEndSeekBarRelease() {
        mTimeSelectView.onProgressRest();

        // 视频指向起始时间点
        long startTime = this.mFrameListOffsetTime + this.mStartSelectSeekOffsetTime;
        playerSeekTo((int) (startTime / 1000));
        playerStart();
    }

    // 结束滑块滑动
    @Override
    public void onEndSeekBarSlide(final long offsetTime) {
        this.mEndSelectSeekOffsetTime = offsetTime;

        // 更新视频指向时间点
        long endTime = this.mFrameListOffsetTime + this.mEndSelectSeekOffsetTime;
        playerSeekTo((int) (endTime / 1000));
    }

    // 边界滑块选择时长
    @Override
    public void onSeekTime(final long totalTime) {
        if (mSelectDurationText != null) {
            Object duration = totalTime / (float) (1000 * 1000);
            mSelectDurationText.setText(String.format(Locale.getDefault(), "%.1fs", duration));
        }
    }

    // 进度滑块滑动
    @Override
    public void onProgressBarChanged(final View view, final long totalTime, final float progressPercent, final boolean fromUser) {
        // 起始滑块代表时间
        long startTime = this.mFrameListOffsetTime + this.mStartSelectSeekOffsetTime;
        // 进度条偏移时间量
        long offsetTime = (long) (totalTime * progressPercent);

        // 滑动视频播放进度
        if (fromUser) {
            playerSeekTo((int) ((startTime + offsetTime) / 1000));
            return;
        }

        // 播放进度到达结尾
        if (offsetTime <= 0 || offsetTime >= totalTime) {
            mTimeSelectView.onProgressRest();
            playerSeekTo((int) (startTime / 1000));
            playerStart();
        }
    }

    // 进度滑块按下
    @Override
    public void onProgressBarTrackingTouch(final View view) {
        playerPause();
    }

    // 进度滑块抬起
    @Override
    public void onProgressBarTrackingRelease(final View view) {
        playerStart();
    }

    // 获取当前视频播放进度时间 相对起始时间选择边界 偏移时间量 us
    @Override
    public long getVideoOffsetPosition() {
        if (mMediaPlayer == null) return 0;
        return mMediaPlayer.getCurrentPosition() * 1000L - this.mFrameListOffsetTime - this.mStartSelectSeekOffsetTime;
    }

    //------------------------------------TextureView回调-------------------------------------------

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
        this.mSurface = new Surface(surface);
        initPlayer();
    }

    @Override
    public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
        this.mSurface = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(final SurfaceTexture surface) {

    }

    //------------------------------------视频播放回调-----------------------------------------------

    /**
     * 视频准备播放监听
     */
    private class PlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mp.setLooping(false);

            // 初始化视频帧解析任务
            initExtractFramesTask();
            // 播放视频
            playerStart();
            isPlayerPrepared = true;
        }
    }

    /**
     * 视频播放完成监听
     */
    private class PlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(final MediaPlayer mp) {
            mTimeSelectView.onProgressRest();

            long startTime = mFrameListOffsetTime + mStartSelectSeekOffsetTime;
            playerSeekTo((int) (startTime / 1000));
            playerStart();
        }
    }

    /**
     * 视频尺寸变化监听
     */
    private class PlayerOnVideoSizeChangedListener implements MediaPlayer.OnVideoSizeChangedListener {
        @Override
        public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
            mVideoSizeWidth = width;
            mVideoSizeHeight = height;

            // 解决拉伸问题
            int viewWidth = mTextureView.getWidth();
            int viewHeight = mTextureView.getHeight();
            Matrix matrix = Utils.getTextureViewSizeCenterMatrix(0, viewWidth, viewHeight, width, height);
            mTextureView.setTransform(matrix);
        }
    }

}
