package cc.buddies.component.common.intent;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * Intent工具类
 */
public class IntentUtils {

    /**
     * 启动应用首页
     * <pre>
     * 配置到清单文件当中：
     * &lt;intent-filter&gt;
     *   &lt;action android:name="${applicationId}.intent.action.HOME" /&gt;
     *   &lt;category android:name="android.intent.category.DEFAULT" &gt;
     * &lt;/intent-filter&gt;
     * </pre>
     *
     * @param packageName 应用包名
     * @return Intent
     */
    public static Intent makeHomeActivity(@NonNull String packageName) {
        String action = packageName + ".intent.action.HOME";
        return makeActionActivity(action);
    }

    /**
     * 启动指定intent-action页面
     *
     * @param action intent action。
     * @return Intent
     */
    public static Intent makeActionActivity(String action) {
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

}
