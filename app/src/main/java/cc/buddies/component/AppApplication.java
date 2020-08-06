package cc.buddies.component;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import cc.buddies.component.observer.ApplicationObserver;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 观察app前后台状态
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
