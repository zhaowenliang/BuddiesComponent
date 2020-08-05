package cc.buddies.component.videoeditor.retriever.hardware.listener;

import android.media.MediaCodec;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import cc.buddies.component.videoeditor.retriever.hardware.ExtractVideoFrameConfig;

public interface OnExtractFramePreparedListener {

    /**
     * 准备完成
     *
     * @param codec  MediaCodec
     * @param format MediaFormat
     */
    void onExtractFramePrepared(@NonNull MediaCodec codec, @NonNull MediaFormat format, @NonNull ExtractVideoFrameConfig config);

}
