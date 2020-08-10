package cc.buddies.component.utils;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class WebViewUtils {

    private static final String TAG = "WebViewUtils";

    // JS方法调用头
    private static String JS_METHOD_HEADER = "javascript:";

    private static void log(String text) {
        Log.d(TAG, "--> " + text);
    }

    /**
     * WebView调用JS方法
     *
     * @param webView    WebView
     * @param methodName JS方法
     * @param params     方法参数json
     */
    public static void sendJS(final WebView webView, final String methodName, final String params) {
        if (webView != null) {
            log("sendJS() --> methodName: " + methodName + "  params: " + params);
            Handler handler = webView.getHandler();
            if (handler == null) {
                webView.evaluateJavascript(JS_METHOD_HEADER + methodName + "(" + params + ")", null);
                return;
            }
            // @JavascriptInterface注入的方法调用在子线程，需要使用webView.getHandler()运行js方法。
            handler.post(() -> webView.evaluateJavascript(JS_METHOD_HEADER + methodName + "(" + params + ")", null));
        }
    }

    /**
     * 获取callback中回调方法
     *
     * @param callback js callback
     * @return js method
     */
    @NonNull
    public static String getCallBackBlock(String callback) {
        try {
            JSONObject jsonObject = new JSONObject(callback);
            return jsonObject.optString("block");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
