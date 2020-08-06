package cc.buddies.component.common.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单线程线程池
 */
public class CustomSingleThreadExecutor {

    private static volatile CustomSingleThreadExecutor instance;
    private ThreadPoolExecutor singleThreadExecutor;

    public static CustomSingleThreadExecutor getInstance() {
        if (instance == null) {
            synchronized (CustomSingleThreadExecutor.class) {
                if (instance == null) {
                    instance = new CustomSingleThreadExecutor();
                }
            }
        }
        return instance;
    }

    private CustomSingleThreadExecutor() {
        singleThreadExecutor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), CustomThreadFactory.DEFAULT_FACTORY.INSTANCE);
        // 核心线程应用超时事件
        singleThreadExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 获取线程池
     *
     * @return ThreadPoolExecutor
     */
    public ThreadPoolExecutor getExecutor() {
        return this.singleThreadExecutor;
    }

}
