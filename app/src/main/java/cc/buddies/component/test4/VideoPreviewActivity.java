package cc.buddies.component.test4;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cc.buddies.component.R;
import cc.buddies.component.videoeditor.Utils;

import java.io.File;
import java.io.IOException;

public class VideoPreviewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = "编辑视频预览";

    private TextureView mTextureView;
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;

    private String mVideoPath;

    // 播放器是否已经准备好播放
    private boolean isPlayerPrepared = false;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_video_preview);

        this.mVideoPath = getIntent().getStringExtra("video_path");

        if (TextUtils.isEmpty(this.mVideoPath)) {
            finish();
            return;
        }

        this.mTextureView = findViewById(R.id.texture_view);
        this.mTextureView.setSurfaceTextureListener(this);

        findViewById(R.id.btn_cancel).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VideoFrames2Activity.class);
            intent.putExtra("video_path", mVideoPath);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlayerPrepared) {
            playerStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file = new File(mVideoPath);
        if (file.exists() && file.delete()) {
            Log.d(TAG, "放弃视频: " + mVideoPath);
        }
    }

    // 初始化播放器
    private void initPlayer() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mVideoPath);
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnVideoSizeChangedListener(this);

                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerStart() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    private void playerPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

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

    @Override
    public void onPrepared(final MediaPlayer mp) {
        isPlayerPrepared = true;
        mp.setLooping(true);
        mp.start();
    }

    @Override
    public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
        // 解决拉伸问题
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();
        Matrix matrix = Utils.getTextureViewSizeCenterMatrix(0, viewWidth, viewHeight, width, height);
        mTextureView.setTransform(matrix);
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {

    }

}
