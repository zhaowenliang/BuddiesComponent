package cc.buddies.component.network.interfaces;

import androidx.annotation.NonNull;

/**
 * 请求响应接口。
 *
 * @param <T> 响应数据泛型
 */
public interface ResponseListener<T> {

    /**
     * 请求成功回调
     *
     * @param message 数据说明
     * @param data    T 解析后的数据对象
     */
    void onSuccess(String message, T data);

    /**
     * 请求返回错误码处理
     *
     * @param code    错误码
     * @param message 错误信息
     */
    void onError(int code, String message);

    /**
     * 请求失败回调
     *
     * @param t 异常
     */
    void onFailure(@NonNull Throwable t);

}
