package cc.buddies.component.common.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 设置富文本样式
 */
public class SpannableUtils {

    /**
     * 设置富文本样式
     *
     * @param spanStr 富文本
     * @param mark    标记
     * @param object  样式（ForegroundColorSpan、ClickableSpan...）
     */
    public static void spannable(@Nullable SpannableString spanStr, @Nullable String mark, @Nullable Object object) {
        if (checkParamsInvalid(spanStr, mark, object)) {
            return;
        }

        String text = spanStr.toString();
        int start = text.indexOf(mark);
        if (start >= 0) {
            spanStr.setSpan(object, start, start + mark.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 设置富文本颜色
     *
     * @param text      文本
     * @param mark      标记文本
     * @param markColor 标记颜色
     * @return SpannableString
     */
    @NonNull
    public static SpannableString spanColor(@Nullable String text, @Nullable String mark, @ColorInt int markColor) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        if (TextUtils.isEmpty(mark)) {
            return new SpannableString(text);
        }

        SpannableString spanStr = new SpannableString(text);
        spannable(spanStr, mark, new ForegroundColorSpan(markColor));
        return spanStr;
    }

    /**
     * 设置富文本点击
     *
     * @param text          文本
     * @param mark          标记文本
     * @param clickableSpan 点击事件
     * @return 富文本
     */
    @NonNull
    public static SpannableString spanClick(@Nullable String text, @Nullable String mark, @Nullable final ClickableSpan clickableSpan) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        if (TextUtils.isEmpty(mark)) {
            return new SpannableString(text);
        }

        SpannableString spannableString = new SpannableString(text);
        spannable(spannableString, mark, clickableSpan);
        return spannableString;
    }

    // 检测参数是否无效
    private static boolean checkParamsInvalid(@Nullable SpannableString spanStr, @Nullable String mark, @Nullable Object object) {
        if (spanStr == null || mark == null || object == null) {
            return true;
        }

        String text = spanStr.toString();
        int start = text.indexOf(mark);
        if (start < 0 || start >= text.length()) {
            return true;
        }

        int end = start + mark.length();
        //noinspection RedundantIfStatement
        if (end < 0 || end >= text.length()) {
            return true;
        }

        return false;
    }

}
