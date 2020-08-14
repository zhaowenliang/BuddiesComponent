package cc.buddies.component.common.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Handler处理后消息消息回调，主要是使用弱引用Context，防止内容泄漏。
 * <p>使用此Handler需要实现接口{@link WeakHandlerCallback}
 *
 * @param <T> 泛型
 */
public class WeakHandler<T extends WeakHandlerCallback> extends Handler {

    private WeakReference<T> weakReference;

    public WeakHandler(T handlerInterface) {
        super(Looper.getMainLooper());
        this.weakReference = new WeakReference<>(handlerInterface);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (weakReference.get() != null) {
            weakReference.get().dealMessage(msg);
        }
    }

}
