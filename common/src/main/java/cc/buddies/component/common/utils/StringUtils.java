package cc.buddies.component.common.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;

public class StringUtils {

    /**
     * 获取32位uuid
     */
    @NonNull
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * float转百分比
     */
    @NonNull
    public static String getPercentString(float percent) {
        return String.format(Locale.getDefault(), "%d%%", (int) (percent * 100));
    }

    /**
     * 拼接字符串
     *
     * @param objects 任意对象
     * @return String
     */
    @NonNull
    public static String getString(@Nullable Object... objects) {
        if (objects == null) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : objects) {
            stringBuilder.append(obj);
        }
        return stringBuilder.toString();
    }

    /**
     * String文本转化Boolean类型
     *
     * @param text 文本
     * @return boolean
     */
    public static boolean getBoolean(@Nullable String text) {
        return "true".equals(text) || "1".equals(text);
    }

    /**
     * 字符串去null并去trim
     *
     * @param str String
     * @return String
     */
    @NonNull
    public static String trimToEmpty(@Nullable String str) {
        return emptyIfNull(str).trim();
    }

    /**
     * 字符串去null
     *
     * @param str String
     * @return String
     */
    @NonNull
    public static String emptyIfNull(@Nullable String str) {
        return str == null ? "" : str;
    }

    /**
     * 获取文件全路径中的文件名称（不包含后缀）
     *
     * @param path 文件路径
     * @return 文件名称
     */
    @NonNull
    public static String getFileName(@Nullable String path) {
        String wholeFileName = getFileNameWithSuffix(path);

        int suffixIndex = wholeFileName.lastIndexOf(".");
        return suffixIndex < 0 ? wholeFileName : wholeFileName.substring(0, suffixIndex);
    }

    /**
     * 获取文件全路径中的文件名称（包含后缀）
     *
     * @param path 文件路径
     * @return 文件名称
     */
    @NonNull
    public static String getFileNameWithSuffix(@Nullable String path) {
        if (path == null || path.trim().length() == 0) return "";

        int index = path.lastIndexOf("/");
        return index < 0 ? path : path.substring(index + 1);
    }

    /**
     * 判断是否为中文字符
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 转换中文为unicode字符
     *
     * @param str 中文文本
     * @return unicode
     */
    @NonNull
    public static String gbEncoding(@Nullable String str) {
        if (str == null) return "";

        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : str.toCharArray()) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }

}
