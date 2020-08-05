package cc.buddies.component.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.IOException;

public class Utils {

    /**
     * 检测视频轨道MediaFormat是否支持当前设备硬解码
     * @param videoFormat 视频轨道MediaFormat
     * @return boolean
     */
    public static boolean isSupportedDecoder(MediaFormat videoFormat) {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String decoderName = mediaCodecList.findDecoderForFormat(videoFormat);
        return !TextUtils.isEmpty(decoderName);
    }

    /**
     * 获取MediaExtractor视频轨道对应MediaFormat
     *
     * @param extractor   MediaExtractor
     * @param targetTrack 目标track. eg: video/ audio/
     * @return -1为未找到
     */
    public static int getExtractorMediaTrackIndex(MediaExtractor extractor, String targetTrack) {
        for (int i = 0, count = extractor.getTrackCount(); i < count; i++) {
            MediaFormat trackFormat = extractor.getTrackFormat(i);
            String mime = trackFormat.getString(MediaFormat.KEY_MIME);

            if (mime != null && mime.toLowerCase().startsWith(targetTrack)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取视频精确时长(us)
     *
     * @param path 文件路径
     * @return us
     */
    public static long getVideoDuration(String path) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(path);

            int trackIndex = getExtractorMediaTrackIndex(extractor, "video/");
            extractor.selectTrack(trackIndex);
            MediaFormat trackFormat = extractor.getTrackFormat(trackIndex);

            return trackFormat.containsKey(MediaFormat.KEY_DURATION) ? trackFormat.getLong(MediaFormat.KEY_DURATION) : 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            extractor.release();
        }
    }

    /**
     * 获取视频旋转角度
     *
     * @param trackFormat MediaExtractor硬解码方式
     * @param path        如果系统不支持硬解码获取，则使用MediaMetadataRetriever获取。
     * @return rotation
     */
    public static int getVideoRotation(MediaFormat trackFormat, String path) {
        int rotation = 0;
        if (trackFormat != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (trackFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                rotation = trackFormat.getInteger(MediaFormat.KEY_ROTATION);
            }
            return rotation;
        }

        // SDK小于23 或者 没有传入MediaFormat
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            String strRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (!TextUtils.isEmpty(strRotation)) {
                rotation = Integer.parseInt(strRotation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        return rotation;
    }

    /**
     * 初始图片缩放旋转
     *
     * @param bitmap   Bitmap
     * @param scale    缩放比例
     * @param rotation 旋转角度
     * @return Bitmap
     */
    private Bitmap dealBitmap(Bitmap bitmap, float scale, int rotation) {
        // 如果不缩放，也不旋转，则不处理
        if (scale == 1 && rotation % 360 == 0) return bitmap;

        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        int dstWidth = (int) (srcWidth * scale);
        int dstHeight = (int) (srcHeight * scale);

        // 解析后的图片压缩旋转
        // 传入bitmap宽高旋转角度和目标宽高，最后返回结果为缩放并旋转后的。
        Matrix transformationMatrix = getTransformationMatrix(srcWidth, srcHeight, dstWidth, dstHeight, rotation, false, false, true);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformationMatrix, true);
    }

    /**
     * 得出一个缩放旋转变换的矩阵
     *
     * @param srcWidth            原始宽
     * @param srcHeight           原始高
     * @param dstWidth            缩放后宽(最终结果要根据角度变换)
     * @param dstHeight           缩放后高(最终结果要根据角度变换)
     * @param applyRotation       变换角度(必须为90的倍数)
     * @param flipHorizontal      是否水平翻转
     * @param flipVertical        是否竖直翻转
     * @param maintainAspectRatio 是否保持缩放比例
     * @return 根据需求得出的变换矩阵
     */
    public static Matrix getTransformationMatrix(
            final int srcWidth,
            final int srcHeight,
            final int dstWidth,
            final int dstHeight,
            final int applyRotation,
            boolean flipHorizontal, boolean flipVertical, final boolean maintainAspectRatio) {

        // 旋转角度必须为90的倍数
        if (applyRotation % 90 != 0) {
            throw new IllegalArgumentException(String.format("旋转角度 %d 必须为90的倍数", applyRotation));
        }

        // 是否旋转缩放
        boolean isRotation = (Math.abs(applyRotation) + 90) % 180 == 0;
        boolean isScale = (srcWidth != dstWidth) || (srcHeight != dstHeight);

        final Matrix matrix = new Matrix();
        // 1. 将图片的中心移动到坐标系的中心(左上角)
        matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

        // 2. 旋转角度
        if (applyRotation != 0) {
            matrix.postRotate(applyRotation);
        }

        // 3. 缩放
        if (isScale) {
            // 缩放因子
            float scaleFactorX = dstWidth / (float) srcWidth;
            float scaleFactorY = dstHeight / (float) srcHeight;

            // 如果进行了90旋转，宽高缩放因子互换。
            if (isRotation) {
                float temp = scaleFactorX;
                //noinspection SuspiciousNameCombination
                scaleFactorX = scaleFactorY;
                scaleFactorY = temp;
            }

            if (maintainAspectRatio) {
                // 保持宽高比，缩放因子为宽高缩放中较大的一个
                final float scaleFactor = Math.max(Math.abs(scaleFactorX), Math.abs(scaleFactorY));
                matrix.postScale(scaleFactor, scaleFactor);
            } else {
                matrix.postScale(scaleFactorX, scaleFactorY);
            }
        }

        // 4. 将移动位置移回去
        float dx = (isRotation ? dstHeight : dstWidth) / 2.0f;
        float dy = (isRotation ? dstWidth : dstHeight) / 2.0f;
        matrix.postTranslate(dx, dy);

        // 5. 根据中心点来做翻转
        int flipHorizontalFactor = flipHorizontal ? -1 : 1;
        int flipVerticalFactor = flipVertical ? -1 : 1;
        matrix.postScale(flipHorizontalFactor, flipVerticalFactor, dx, dy);

        return matrix;
    }

    // 重新计算video的显示位置，让其全部显示并据中
    public static Matrix getTextureViewSizeCenterMatrix(int degree, float viewWidth, float viewHeight, int videoWidth, int videoHeight) {
        // 宽高缩放比例，根据横竖屏有不同。
        float sx = (degree % 180 == 0) ? viewWidth / (float) videoWidth : viewHeight / (float) videoWidth;
        float sy = (degree % 180 == 0) ? viewHeight / (float) videoHeight : viewWidth / (float) videoHeight;

        /* 关于pre/post/set，pre方法倒序执行，post方法顺序执行，set方法覆盖之前的操作。 */
        /* 以下流程先执行第2步校正宽高比，再执行第一步移动中心点，再执行第三步缩放宽高到屏幕内完全显示。 */

        Matrix matrix = new Matrix();

        // 第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((viewWidth - videoWidth) / 2, (viewHeight - videoHeight) / 2);

        // 第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(videoWidth / viewWidth, videoHeight / viewHeight);

        // 第3步:等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy) {
            matrix.postScale(sy, sy, viewWidth / 2, viewHeight / 2);
        } else {
            matrix.postScale(sx, sx, viewWidth / 2, viewHeight / 2);
        }

        matrix.postRotate(degree % 360, viewWidth / 2, viewHeight / 2);
        return matrix;
    }

    // 折半查找数组匹配值索引
    public static int binarySearch(int[] arr, int key) {
        int start = 0;
        int end = arr.length - 1;//8
        while (start <= end) {
            int middle = (start + end) / 2;//中间值:4,1,2
            if (key < arr[middle]) {
                end = middle - 1;
            } else if (key > arr[middle]) {
                start = middle + 1;
            } else {
                return middle;
            }
        }
        return -1;
    }

    // 折半查找数组匹配或最接近的值索引
    public static int binarySearchNear(long[] arr, long key) {
        int start = 0;
        int end = arr.length - 1;

        int middle = 0;
        while (start <= end) {
            middle = (start + end) / 2;
            if (key < arr[middle]) {
                end = middle - 1;
            } else if (key > arr[middle]) {
                start = middle + 1;
            } else {
                break;
            }
        }

        int min = middle;
        if (middle > 0) {
            if (Math.abs(arr[middle - 1] - key) < Math.abs(arr[middle] - key)) {
                min = middle - 1;
            }
        }

        if (middle < arr.length - 1) {
            if (Math.abs(arr[middle + 1] - key) < Math.abs(arr[min] - key)) {
                min = middle + 1;
            } else {
                min = middle;
            }
        }

        return min;
    }

//    public static void printMediaCodecInfo() {
//        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
//        MediaCodecInfo[] codecInfos = mediaCodecList.getCodecInfos();
//        Log.d("aaaa", "MediaCodecInfo length : " + codecInfos.length);
//
//        for (MediaCodecInfo codecInfo : codecInfos) {
//            String name = codecInfo.getName();
//            Log.d("aaaa", "MediaCodecInfo name : " + name);
//
//            String[] supportedTypes = codecInfo.getSupportedTypes();
//            for (String supportedType : supportedTypes) {
//                Log.d("aaaa", "MediaCodecInfo supportedType : " + supportedType);
//            }
//
//            Log.d("aaaa", "MediaCodecInfo ------------------------------------------");
//        }
//    }

}
