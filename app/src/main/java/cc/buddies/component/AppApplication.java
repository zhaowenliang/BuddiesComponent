package cc.buddies.component;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import cc.buddies.component.observer.ApplicationObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 观察app前后台状态
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());

        // 捕获RxJava未处理的抛出异常 Functions.ON_ERROR_MISSING，如果手动实现了处理异常则不执行此处。
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
