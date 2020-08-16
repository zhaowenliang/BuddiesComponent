package cc.buddies.component.storage.preference;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 默认的SharePreferences.
 * <br/>默认名称为package+"_preferences".
 * <br/>在此默认SP文件内可以放置一些全局公共配置，如果是某个模块/业务单独的配置，需要创建新的SP文件。
 * <pre>eg:
 *     DefaultPreferences preferences = new DefaultPreferences(context);
 *     preferences.getSharedPreferences().edit().putString("aaa", "123").apply();
 * </pre>
 */
public class DefaultPreferences extends BasePreferences {

    private WeakReference<Context> contextWeakReference;

    public DefaultPreferences(@NonNull Context context) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
    }

    @Override
    protected Context initContext() {
        return contextWeakReference != null ? contextWeakReference.get() : null;
    }

    @Override
    protected String initName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return PreferenceManager.getDefaultSharedPreferencesName(initContext());
        } else {
            return initContext().getPackageName() + "_preferences";
        }
    }

}
