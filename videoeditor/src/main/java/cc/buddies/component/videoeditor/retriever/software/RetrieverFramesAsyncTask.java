package cc.buddies.component.videoeditor.retriever.software;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.LongSparseArray;

import androidx.annotation.IntDef;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import cc.buddies.component.videoeditor.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 软解码视频帧异步任务
 */
public class RetrieverFramesAsyncTask extends AsyncTask<Long, Bitmap, Integer> {

    private MediaMetadataRetriever retriever;
    private RetrieverFramesCallback callback;
    private LongSparseArray<Bitmap> sparseArray = new LongSparseArray<>();

    private int width;
    private int height;

    // 解析帧模式
    private int option = MediaMetadataRetriever.OPTION_CLOSEST;

    @IntDef(flag = true, value = {
            MediaMetadataRetriever.OPTION_PREVIOUS_SYNC,
            MediaMetadataRetriever.OPTION_NEXT_SYNC,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
            MediaMetadataRetriever.OPTION_CLOSEST,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Option {
    }

    // 使用MediaMetadataRetriever.OPTION_CLOSEST的方式解析帧，有的视频有很多帧获取到为空。华为nova3e设备拍摄视频是这样。
    // 使用MediaMetadataRetriever.OPTION_CLOSEST_SYNC解析帧没问题，而且很快，但是只能获取关键帧。
    public void setOption(@Option int option) {
        this.option = option;
    }

    public void setRect(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public RetrieverFramesAsyncTask(final RetrieverFramesCallback callback, final String path) {
        this.callback = callback;
        this.retriever = new MediaMetadataRetriever();
        this.retriever.setDataSource(path);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onPreExecute();
        }
    }

    @Override
    protected Integer doInBackground(final Long... longs) {
        // longs[0] 为获取帧间隔时长(us)
        long interval = longs[0];
        long duration = 0;
        String strDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try {
            duration = Long.parseLong(strDuration);
            duration *= 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = (int) (duration / interval);
        for (int i = 0; i <= count; i++) {
            long time = interval * i;
            Bitmap bitmap = retriever.getFrameAtTime(time, option);
            if (bitmap == null) continue;

            Bitmap resultBitmap;
            int srcWidth = bitmap.getWidth();
            int srcHeight = bitmap.getHeight();
            int dstWidth = this.width;
            int dstHeight = this.height;

            // bitmap 缩放旋转 (MediaMetadataRetriever已经自动校正角度)
            if (dstWidth > 0 && dstHeight > 0) {
                Matrix transformationMatrix = Utils.getTransformationMatrix(srcWidth, srcHeight, dstWidth, dstHeight, 0, false, false, true);
                resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformationMatrix, true);
                bitmap.recycle();
            } else {
                resultBitmap = bitmap;
            }

            sparseArray.put(time, resultBitmap);
            publishProgress(resultBitmap);
        }
        return count;
    }

    @Override
    protected void onProgressUpdate(final Bitmap... values) {
        super.onProgressUpdate(values);
        if (callback != null) {
            Bitmap bitmap = values[0];
            long key = sparseArray.keyAt(sparseArray.indexOfValue(bitmap));
            callback.onUpdate(bitmap, key);
        }
    }

    @Override
    protected void onPostExecute(final Integer integer) {
        super.onPostExecute(integer);
        if (this.retriever != null) {
            this.retriever.release();
        }
        if (callback != null) {
            callback.onCompleted(integer);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.retriever != null) {
            this.retriever.release();
        }
        if (callback != null) {
            callback.onCancelled();
        }
    }


    public interface RetrieverFramesCallback {

        @MainThread
        void onPreExecute();

        @MainThread
        void onUpdate(@NonNull Bitmap bitmap, long time);

        @MainThread
        void onCompleted(int count);

        @MainThread
        void onCancelled();
    }

}