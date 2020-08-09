package cc.buddies.component.network.interfaces;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 请求响应接口。
 *
 * @param <T> 响应数据泛型
 */
public interface ResponseContextListener<T> {

    /**
     * 请求成功回调
     *
     * @param data T 解析后的数据对象
     */
    void onSuccess(@NonNull Context context, T data);

    /**
     * 请求返回错误码处理
     *
     * @param code    错误码
     * @param message 错误信息
     */
    void onError(@NonNull Context context, int code, String message);

    /**
     * 请求失败回调
     *
     * @param t 异常
     */
    void onFailure(@NonNull Context context, @NonNull Throwable t);

}
