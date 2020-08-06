package cc.buddies.component.common.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程生产工厂，重写系统DefaultThreadFactory。
 */
public class CustomThreadFactory implements ThreadFactory {

    private final ThreadGroup mThreadGroup;
    private final AtomicInteger mThreadNumber = new AtomicInteger(1);
    private final String mNamePrefix;

    public static class DEFAULT_FACTORY {
        public static final CustomThreadFactory INSTANCE = new CustomThreadFactory("CustomThreadFactory");
    }

    public CustomThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        mThreadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        mNamePrefix = name + "#";
    }

    @Override
    public synchronized Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(mThreadGroup, r, mNamePrefix + mThreadNumber.getAndIncrement(), 0);

        // no daemon
        if (t.isDaemon())
            t.setDaemon(false);

        // normal priority
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);

        return t;
    }

}
