package cc.buddies.component.common.thread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 自定义默认的任务执行器
 */
public class CustomDefaultTaskExecutor extends CustomTaskExecutor {

    private final Object mLock = new Object();

    @Nullable
    private volatile Handler mMainHandler;

    @Override
    public void executeOnDiskIO(@NonNull Runnable runnable) {
        CustomThreadExecutor.getInstance().getExecutor().execute(runnable);
    }

    @Override
    public void postToMainThread(@NonNull Runnable runnable) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        //noinspection ConstantConditions
        mMainHandler.post(runnable);
    }

    @Override
    public boolean isMainThread() {
        return ThreadUtils.isMainThread();
    }

}
