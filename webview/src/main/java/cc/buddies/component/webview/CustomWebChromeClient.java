package cc.buddies.component.webview;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import java.lang.ref.WeakReference;

public class CustomWebChromeClient extends WebChromeClient {

    private WeakReference<Context> contextWeakReference;

    public CustomWebChromeClient(Context context) {
        if (context != null) {
            contextWeakReference = new WeakReference<>(context);
        }
    }

    private Context getContext() {
        return contextWeakReference != null && contextWeakReference.get() != null ? contextWeakReference.get() : null;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();
        switch (messageLevel) {
            case TIP:
                Log.v("WebView Console", "-->\n文件: " + consoleMessage.sourceId() + "\n行号: " + consoleMessage.lineNumber() + "\n输出内容: " + consoleMessage.message());
                break;
            case LOG:
                Log.i("WebView Console", "-->\n文件: " + consoleMessage.sourceId() + "\n行号: " + consoleMessage.lineNumber() + "\n输出内容: " + consoleMessage.message());
                break;
            case WARNING:
                Log.w("WebView Console", "-->\n文件: " + consoleMessage.sourceId() + "\n行号: " + consoleMessage.lineNumber() + "\n输出内容: " + consoleMessage.message());
                break;
            case ERROR:
                Log.e("WebView Console", "-->\n文件: " + consoleMessage.sourceId() + "\n行号: " + consoleMessage.lineNumber() + "\n输出内容: " + consoleMessage.message());
                break;
            case DEBUG:
                Log.d("WebView Console", "-->\n文件: " + consoleMessage.sourceId() + "\n行号: " + consoleMessage.lineNumber() + "\n输出内容: " + consoleMessage.message());
                break;
        }
        return true;
    }

}
