package cc.buddies.component.common.handler;

import android.os.Message;

/**
 * {@link WeakHandler} 处理消息回调接口
 */
public interface WeakHandlerCallback {

    void dealMessage(Message message);

}
