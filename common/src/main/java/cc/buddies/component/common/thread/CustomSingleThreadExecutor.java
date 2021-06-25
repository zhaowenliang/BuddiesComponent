package cc.buddies.component.common.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单线程线程池
 */
public class CustomSingleThreadExecutor {

    private static volatile CustomSingleThreadExecutor sInstance;

    @NonNull
    private final ThreadPoolExecutor singleThreadExecutor;

    @NonNull
    public static CustomSingleThreadExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (CustomSingleThreadExecutor.class) {
            if (sInstance == null) {
                sInstance = new CustomSingleThreadExecutor();
            }
        }
        return sInstance;
    }

    private CustomSingleThreadExecutor() {
        singleThreadExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("CustomSingleThreadExecutor"));
        // 核心线程应用超时事件
        singleThreadExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 获取线程池
     *
     * @return ThreadPoolExecutor
     */
    @NonNull
    public ThreadPoolExecutor getExecutor() {
        return this.singleThreadExecutor;
    }

}
