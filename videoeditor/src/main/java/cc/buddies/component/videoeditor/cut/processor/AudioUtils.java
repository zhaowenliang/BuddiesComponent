package cc.buddies.component.videoeditor.cut.processor;

import android.annotation.SuppressLint;
import android.media.MediaFormat;

import cc.buddies.component.videoeditor.cut.VideoCutAsyncTask;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AudioUtils {

    private static final String TAG = "AudioUtils";

    @SuppressLint("UseSparseArrays")
    private final static Map<Integer, Integer> freqIdxMap = new HashMap<>();

    static {
        freqIdxMap.put(96000, 0);
        freqIdxMap.put(88200, 1);
        freqIdxMap.put(64000, 2);
        freqIdxMap.put(48000, 3);
        freqIdxMap.put(44100, 4);
        freqIdxMap.put(32000, 5);
        freqIdxMap.put(24000, 6);
        freqIdxMap.put(22050, 7);
        freqIdxMap.put(16000, 8);
        freqIdxMap.put(12000, 9);
        freqIdxMap.put(11025, 10);
        freqIdxMap.put(8000, 11);
        freqIdxMap.put(7350, 12);
    }


    public static int getAudioBitrate(MediaFormat format) {
        if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
            return format.getInteger(MediaFormat.KEY_BIT_RATE);
        } else {
            return VideoCutAsyncTask.DEFAULT_AAC_BITRATE;
        }
    }

    public static int getAudioMaxBufferSize(MediaFormat format) {
        if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            return format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        } else {
            return 100 * 1000;
        }
    }

    public static void checkCsd(MediaFormat audioMediaFormat, int profile, int sampleRate, int channel) {
        //noinspection ConstantConditions
        int freqIdx = freqIdxMap.containsKey(sampleRate) ? freqIdxMap.get(sampleRate) : 4;
//        byte[] bytes = new byte[]{(byte) 0x11, (byte) 0x90};
//        ByteBuffer bb = ByteBuffer.wrap(bytes);
        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put(0, (byte) (profile << 3 | freqIdx >> 1));
        csd.put(1, (byte) ((freqIdx & 0x01) << 7 | channel << 3));
        audioMediaFormat.setByteBuffer("csd-0", csd);
    }

}
