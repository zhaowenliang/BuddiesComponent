package cc.buddies.component.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 基础SharedPreferences操作类
 * <pre>使用方式，例:
 * public class AppPreference extends BasePreferences implements AppPreferenceConfig {
 *
 *     private static final String SP_NAME = APP_SP_NAME;
 *
 *     private AppPreference() {}
 *
 *     public static AppPreference getInstance() {
 *         return AppPreference.SingleTonHolder.PREFERENCES;
 *     }
 *
 *     private static class SingleTonHolder {
 *         private static final AppPreference PREFERENCES = new AppPreference();
 *     }
 *
 *     protected Context initContext() {
 *         return BaseApplication.getApplication();
 *     }
 *
 *     protected String initName() {
 *         return SP_NAME;
 *     }
 *
 *     public void saveUserInfo(String userInfo) {
 *         getSharedPreferences().edit().putString(KEY_USER_INFO, userInfo).apply();
 *     }
 *
 *     public String getUserInfo() {
 *         return getSharedPreferences().getString(KEY_USER_INFO, "");
 *     }
 * }
 * </pre>
 */
public abstract class BasePreferences {

    /**
     * 初始化上下文
     */
    protected abstract Context initContext();

    /**
     * 初始化SP名称
     */
    protected abstract String initName();

    protected int getPreferenceMode() {
        return Context.MODE_PRIVATE;
    }

    /**
     * 获取可操作的SharedPreferences
     *
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreferences() {
        Context context = initContext();
        return context.getSharedPreferences(initName(), getPreferenceMode());
    }

}
