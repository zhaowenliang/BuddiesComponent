package cc.buddies.component.common.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    /**
     * Bitmap --> Drawable
     */
    public static Drawable bitmap2Drawable(Resources resources, Bitmap sourceBmp) {
        return new BitmapDrawable(resources, sourceBmp);
    }

    /**
     * Drawable --> Bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable sourceDrawable) {
        return ((BitmapDrawable) sourceDrawable).getBitmap();
    }

    /**
     * Drawable --> Bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable sourceDrawable, int bmpWidth, int bmpHeight) {
        Bitmap bmp = ((BitmapDrawable) sourceDrawable).getBitmap();
        if (bmp.getWidth() > bmpWidth || bmp.getHeight() > bmpHeight) {
            return Bitmap.createScaledBitmap(bmp, bmpWidth, bmpHeight, false);
        }
        return bmp;
    }

    /**
     * String --> Bitmap
     */
    @Nullable
    public static Bitmap string2Bmp(String imgBase64Str) {
        try {
            byte[] bitmapArray = Base64.decode(imgBase64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Bitmap --> String
     */
    public static String bitmap2String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Bitmap --> File
     */
    public static File bitmap2File(Bitmap bmp, String newFilePath) {
        byte[] byteArray = bmp2ByteArray(bmp);
        bmp.recycle();
        return byteArray2File(byteArray, newFilePath);
    }

    /**
     * Bitmap --> ScaledBitmap
     */
    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight) {
        return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    }

    /**
     * Bitmap --> ScaledBitmap
     */
    public static Bitmap createScaledBitmap(String filePath, int dstWidth, int dstHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, dstWidth, dstHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * Rotate Bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Bitmap b;
        if ((degree != 0) && (bitmap != null)) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, bitmap.getWidth() / 2.0F, bitmap.getHeight() / 2.0F);
            b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmap != b)
                bitmap.recycle();
        } else {
            b = bitmap;
        }
        return b;
    }

    private static byte[] bmp2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private static File byteArray2File(byte[] byteArray, String newFilePath) {
        File imageFile = new File(newFilePath);
        FileOutputStream fstream = null;
        BufferedOutputStream bStream = null;
        try {
            fstream = new FileOutputStream(imageFile);
            bStream = new BufferedOutputStream(fstream);
            bStream.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bStream != null) {
                try {
                    bStream.flush();
                    bStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fstream != null) {
                try {
                    fstream.flush();
                    fstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageFile;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = Math.min(heightRatio, widthRatio);
        }

        return inSampleSize;
    }
}
