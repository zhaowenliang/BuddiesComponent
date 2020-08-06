package cc.buddies.component.observer;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * App前后台状态变化观察者
 * <pre>
 *     在Application的onCreate方法中添加观察
 *     ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
 *     使用到了"androidx.lifecycle:lifecycle-process:version"库
 * </pre>
 */
public class ApplicationObserver implements LifecycleObserver {

    private String TAG = this.getClass().getSimpleName();

    /**
     * ON_CREATE 在应用程序的整个生命周期中只会被调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.d(TAG, "Lifecycle.Event.ON_CREATE");
    }

    /**
     * 应用程序出现到前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.d(TAG, "Lifecycle.Event.ON_START");
    }

    /**
     * 应用程序出现到前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.d(TAG, "Lifecycle.Event.ON_RESUME");
    }

    /**
     * 应用程序退出到后台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "Lifecycle.Event.ON_PAUSE");
    }

    /**
     * 应用程序退出到后台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.d(TAG, "Lifecycle.Event.ON_STOP");
    }

    /**
     * 永远不会被调用到，系统不会分发调用ON_DESTROY事件
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "Lifecycle.Event.ON_DESTROY");
    }
}
