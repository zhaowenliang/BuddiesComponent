package cc.buddies.component.common.listener;

import android.os.SystemClock;
import android.view.View;

/**
 * 点击事件防抖动，扩展{@link View.OnClickListener}接口(JAVA_1.8特性)。
 */
@FunctionalInterface
public interface DebounceOnClickListener extends View.OnClickListener {

    // 使用数组记录每次点击时间
    long[] HINTS = new long[2];

    // 防抖动时长(ms)
    int DEBOUNCE_TIME = 500;

    @Override
    default void onClick(View v) {
        if (onDebounce()) {
            doClick(v);
        }
    }

    /**
     * 去抖动处理
     * @return 是否下发点击事件
     */
    default boolean onDebounce() {
        // 将mHints数组内的所有元素左移一个位置
        System.arraycopy(HINTS, 1, HINTS, 0, HINTS.length - 1);

        // 获得当前系统已经启动的时间，放入数组最后一个位置
        HINTS[HINTS.length - 1] = SystemClock.uptimeMillis();

        // 根据两次点击时间差判断抖动
        return HINTS[0] <= HINTS[HINTS.length - 1] - DEBOUNCE_TIME;
    }

    /**
     * 点击事件回调，相当于{@link View.OnClickListener#onClick(View)}
     * @param v 点击View
     */
    void doClick(View v);

}