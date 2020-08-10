package cc.buddies.component.webview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

/**
 * WebView WebViewClient
 */
public class CustomWebViewClient extends WebViewClient {

    private static final String TAG = "CustomWebViewClient";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // 设定开始加载操作
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // 设定加载结束操作
    }

    // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        // 设定加载资源操作
    }

    // 加载页面的服务器出现错误时（如404）调用。
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG, "failingUrl: " + failingUrl + "     errorCode: " + errorCode);
    }

    // 如果页面中有资源未获取到返回404也会调用这里。
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    // webView默认是不处理https请求的，页面显示空白，在此设置处理https请求。
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        handler.proceed();      // 表示等待证书响应
        // handler.cancel();       // 表示挂起连接，默认方式
        // handler.handleMessage(null);     // 可做其他处理
    }

}
