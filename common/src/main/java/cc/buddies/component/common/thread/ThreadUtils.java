package cc.buddies.component.common.thread;

import android.os.Looper;

/**
 * 线程工具
 */
public class ThreadUtils {

    /**
     * 判断当前线程是否是主线程
     * @return boolean
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

}
