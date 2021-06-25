package cc.buddies.component.common.thread;

import androidx.annotation.NonNull;

/**
 * 自定义任务执行器抽象类
 */
public abstract class CustomTaskExecutor {

    /**
     * 执行任务在IO线程
     *
     * @param runnable 任务
     */
    public abstract void executeOnDiskIO(@NonNull Runnable runnable);

    /**
     * 发送任务到主线程执行
     *
     * @param runnable 任务
     */
    public abstract void postToMainThread(@NonNull Runnable runnable);

    /**
     * 执行任务在主线程
     *
     * @param runnable 任务
     */
    public void executeOnMainThread(@NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMainThread(runnable);
        }
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return 是否是主线程
     */
    public abstract boolean isMainThread();

}
