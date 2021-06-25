package cc.buddies.component.common.thread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;

/**
 * 自定义可配置的任务执行器
 */
public class CustomArchTaskExecutor extends CustomTaskExecutor {

    private static volatile CustomArchTaskExecutor sInstance;

    @NonNull
    private final CustomTaskExecutor mDefaultTaskExecutor;  // 默认任务执行器

    @NonNull
    private CustomTaskExecutor mDelegate;   // 可配置的代理执行器（默认使用默认执行器）

    @NonNull
    private static final Executor sMainThreadExecutor = command -> getInstance().postToMainThread(command);

    @NonNull
    private static final Executor sIOThreadExecutor = command -> getInstance().executeOnDiskIO(command);

    private CustomArchTaskExecutor() {
        mDefaultTaskExecutor = new CustomDefaultTaskExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    @NonNull
    public static CustomArchTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (CustomArchTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new CustomArchTaskExecutor();
            }
        }
        return sInstance;
    }

    public void setDelegate(@Nullable CustomTaskExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    public void executeOnDiskIO(@NonNull Runnable runnable) {
        mDelegate.executeOnDiskIO(runnable);
    }

    @Override
    public void postToMainThread(@NonNull Runnable runnable) {
        mDelegate.postToMainThread(runnable);
    }

    @NonNull
    public static Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    @NonNull
    public static Executor getIOThreadExecutor() {
        return sIOThreadExecutor;
    }

    @Override
    public boolean isMainThread() {
        return ThreadUtils.isMainThread();
    }
}
