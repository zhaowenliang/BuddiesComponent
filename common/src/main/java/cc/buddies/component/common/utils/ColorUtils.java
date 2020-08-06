package cc.buddies.component.common.utils;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * 颜色工具类
 */
public class ColorUtils {

    /**
     * 颜色值转化String类型颜色(#FFFFFF)
     *
     * @param color 颜色值
     * @return String类型颜色
     */
    public static String transColorStr(@ColorInt int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        return "#" +
                (alpha.length() < 2 ? "0" + alpha : alpha) +
                (red.length() < 2 ? "0" + red : red) +
                (green.length() < 2 ? "0" + green : green) +
                (blue.length() < 2 ? "0" + blue : blue);
    }

    /**
     * 设置颜色值带透明度
     *
     * @param color 颜色值
     * @param alpha 透明度(相对于255的百分比)
     * @return 带透明度颜色值
     */
    public static int colorWithAlpha(@ColorInt int color, float alpha) {
        return colorWithAlpha(color, (int) (255 * alpha));
    }

    /**
     * 设置颜色值带透明度
     *
     * @param color 颜色值
     * @param alpha 透明度(0~255)
     * @return 带透明度颜色值
     */
    public static int colorWithAlpha(@ColorInt int color, int alpha) {
        return (color & 0xFFFFFF) | (alpha << 24);
    }

    /**
     * 判断颜色是否偏黑色
     *
     * @param color 颜色
     * @param level 级别
     * @return boolean boolean
     */
    public static boolean isBlackColor(int color, int level) {
        int grey = toGrey(color);
        return grey < level;
    }

    /**
     * 颜色转换成灰度值
     * 公式 Gray = R*0.299 + G*0.587 + B*0.114
     *
     * @param rgb 颜色
     * @return 灰度值
     */
    public static int toGrey(int rgb) {
        int blue = rgb & 0x000000FF;
        int green = (rgb & 0x0000FF00) >> 8;
        int red = (rgb & 0x00FF0000) >> 16;
        return (red * 38 + green * 75 + blue * 15) >> 7;
    }

}
