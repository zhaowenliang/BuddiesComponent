package cc.buddies.component.common.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多任务线程池
 */
public class CustomThreadExecutor {

    private static volatile CustomThreadExecutor sInstance;

    @NonNull
    private final ThreadPoolExecutor threadPoolExecutor;

    // 这些配置模仿AsyncTask.
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // 最少2个，最多4个线程在核心线程池中。后台工作的CPU，最好比CPU计数少1以避免饱和，
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

    @NonNull
    public static CustomThreadExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (CustomThreadExecutor.class) {
            if (sInstance == null) {
                sInstance = new CustomThreadExecutor();
            }
        }
        return sInstance;
    }

    /*
        ThreadPoolExecutor构造介绍：
        corePoolSize: 线程池的核心线程数，默认情况下，核心线程会在线程池中一直存活，即使处于闲置状态。
                      但如果将allowCoreThreadTimeOut设置为true的话，那么核心线程也会有超时机制，在keepAliveTime设置的时间过后，核心线程也会被终止。
        maximumPoolSize: 最大的线程数，包括核心线程，也包括非核心线程，在线程数达到这个值后，新来的任务将会被阻塞。
        keepAliveTime: 超时的时间，闲置的非核心线程超过这个时长，将会被销毁回收，当allowCoreThreadTimeOut为true时，这个值也作用于核心线程.
        unit：超时时间的时间单位.
        workQueue：线程池的任务队列，通过execute方法提交的runnable对象会存储在这个队列中.
        threadFactory: 线程工厂, 为线程池提供创建新线程的功能.
        handler: 任务无法执行时，回调handler的rejectedExecution方法来通知调用者.
     */
    private CustomThreadExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, new CustomThreadFactory("CustomThreadExecutor"));
        // 核心线程应用超时事件
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 获取线程池
     *
     * @return ThreadPoolExecutor
     */
    @NonNull
    public ThreadPoolExecutor getExecutor() {
        return this.threadPoolExecutor;
    }

}
