package cc.buddies.component.webview;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import cc.buddies.component.utils.WebViewUtils;

/**
 * 提供JS调用原生方法
 * <p>这里写公共方法，如果是页面私有方法则继承当前类来实现私有方法。
 */
public class WebViewJavaMethodInterface {

    private static final String TAG = "WebJavaMethodInterface";

    // Native方法调用标签
    public static final String NATIVE_METHOD_TAG = "AndroidNative";

    private WeakReference<Context> contextWeakReference;

    public WebViewJavaMethodInterface(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Nullable
    protected Context getContext() {
        return (contextWeakReference != null && contextWeakReference.get() != null) ? contextWeakReference.get() : null;
    }

    protected void log(String text) {
        Log.d(TAG, " --> " + text);
    }

    @JavascriptInterface
    public void Log(String text) {
        log(text);
    }

    /**
     * H5调用 --> 复制信息到粘贴板
     */
    @JavascriptInterface
    public void copy(String label, String text) {
        log("copy() label: " + label + " text: " + text);
        Context context = getContext();
        if (context == null) return;

        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null) return;

        cm.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    /**
     * callback回调方法
     *
     * @param view       WebView
     * @param methodName 方法名block
     * @param data       回调数据
     */
    protected void callbackMethod(WebView view, String methodName, String data) {
        if (view != null && !TextUtils.isEmpty(methodName)) {
            WebViewUtils.sendJS(view, methodName, data);
        }
    }

    /**
     * 调用在主线程
     * <p>使用@JavascriptInterface注入方法调用在子线程，可以使用此方法，将运行内容放到主线程。
     *
     * @param runnable Runnable
     */
    protected void callOnMainThread(WebView view, @NonNull Runnable runnable) {
        if (view != null) {
            view.post(runnable);
        } else if (getContext() instanceof Activity) {
            ((Activity) getContext()).runOnUiThread(runnable);
        }
    }

}
