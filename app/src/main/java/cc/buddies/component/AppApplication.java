package cc.buddies.component;

import android.app.Application;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
