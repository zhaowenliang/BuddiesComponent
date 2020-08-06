package cc.buddies.component.videoeditor.cut.processor;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import cc.buddies.component.videoeditor.VideoEditorUtils;

import java.io.IOException;

public class VideoUtils {

    public static int getFrameRate(String videoPath) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(videoPath);
            int trackIndex = VideoEditorUtils.getExtractorMediaTrackIndex(extractor, "video/");
            MediaFormat format = extractor.getTrackFormat(trackIndex);
            return format.containsKey(MediaFormat.KEY_FRAME_RATE) ? format.getInteger(MediaFormat.KEY_FRAME_RATE) : -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            extractor.release();
        }
    }

    public static boolean trySetProfileAndLevel(MediaCodec codec, String mime, MediaFormat format, int profileInt, int levelInt) {
        MediaCodecInfo codecInfo = codec.getCodecInfo();
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
        MediaCodecInfo.CodecProfileLevel[] profileLevels = capabilities.profileLevels;

        if (profileLevels == null) return false;

        for (MediaCodecInfo.CodecProfileLevel level : profileLevels) {
            if (level.profile == profileInt) {
                if (level.level == levelInt) {
                    format.setInteger(MediaFormat.KEY_PROFILE, profileInt);
                    format.setInteger(MediaFormat.KEY_LEVEL, levelInt);
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static int getMaxSupportBitrate(MediaCodec codec, String mime) {
        try {
            MediaCodecInfo codecInfo = codec.getCodecInfo();
            MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
            return capabilities.getVideoCapabilities().getBitrateRange().getUpper();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
