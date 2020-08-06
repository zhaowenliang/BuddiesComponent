package cc.buddies.component.common.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘弹出关闭工具类
 */
public class KeyBoardUtils {

    /**
     * 改变键盘输入法的状态，如果已经弹出就关闭，如果关闭了就强制弹出
     */
    public static void toggleSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;

        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制关闭软键盘
     */
    public static void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && view != null) {
            if (view.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 弹出软键盘
     */
    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;

        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

//    KeyBoardUtils.closeKeyboard(toActivity(), toActivity().getWindow().getCurrentFocus()); // 强制关闭软件盘

}
