package cc.buddies.component.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Supplier;

public class WebViewFragment extends BaseWebFragment {

    private static String TAG = "BaseWebFragment";

    // H5地址
    private String mUrl;

    private Supplier<WebViewJavaMethodInterface> mGetJavaMethodInterface;

    // 当前页面注入的JS方法对象
    private WebViewJavaMethodInterface mNativeMethodInterface;

    /**
     * 启动WebViewFragment
     *
     * @param url WebView加载地址
     * @return WebViewFragment
     */
    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    public void setGetJavaMethodInterface(Supplier<WebViewJavaMethodInterface> getJavaMethodInterface) {
        this.mGetJavaMethodInterface = getJavaMethodInterface;
    }

    public WebViewJavaMethodInterface getNativeMethodInterface() {
        return mNativeMethodInterface;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mUrl = getArguments().getString("url", "");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
        initWebViewComplete();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebView webView = getWebView();
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 设置背景透明
        webView.setBackgroundColor(0);

        WebSettings webSettings = webView.getSettings();

        // 支持JavaScript交互
        webSettings.setJavaScriptEnabled(true);

        // 开启本地DOM存储，android6.0以下版本加载h5页面之后，h5里面的方法不能被触发，也就是点击没反应
        webSettings.setDomStorageEnabled(true);

        // 自适应屏幕
        webSettings.setUseWideViewPort(true);       // 设置是否“viewport”的 HTML meta tag，这个标识是用来屏幕自适应的
        webSettings.setLoadWithOverviewMode(true);  // 缩放至屏幕大小

        // 缩放操作
        webSettings.setSupportZoom(false);           // 支持缩放，默认为true，是下面缩放控件的前提。
        webSettings.setBuiltInZoomControls(false);   // 使用内置缩放控件，若为false，则不可缩放。
        webSettings.setDisplayZoomControls(false);  // 隐藏原生的缩放控件

        // 其他
        webSettings.setAllowFileAccess(true);                           // 设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);     // 支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);                  // 支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");                // 设置编码格式

        if (mGetJavaMethodInterface == null) {
            mNativeMethodInterface = new WebViewJavaMethodInterface(getContext());
        } else {
            mNativeMethodInterface = mGetJavaMethodInterface.get();
        }

        webView.addJavascriptInterface(mNativeMethodInterface, WebViewJavaMethodInterface.NATIVE_METHOD_TAG);

        webView.setWebChromeClient(getWebChromeClient());

        webView.setWebViewClient(getWebViewClient());
    }

    private void loadUrl() {
        Log.d(TAG, "WebViewFragment --> loadUrl: " + mUrl);
        if (getWebView() != null) {
            getWebView().loadUrl(mUrl);
        }
    }

    protected void initWebViewComplete() {
        // 初始化WebView配置完成
    }

    protected WebChromeClient getWebChromeClient() {
        return new WebChromeClient();
    }

    protected WebViewClient getWebViewClient() {
        return new WebViewClient();
    }

}
