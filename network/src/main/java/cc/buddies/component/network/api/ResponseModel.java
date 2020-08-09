package cc.buddies.component.network.api;

import java.io.Serializable;

/**
 * http请求响应基本结构
 *
 * @param <T> 数据体内数据对象类型
 */
public class ResponseModel<T> implements Serializable {

    /**
     * 请求响应状态码
     */
    private int code;

    /**
     * 请求响应提示信息
     */
    private String message;

    /**
     * 请求响应数据体
     */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
