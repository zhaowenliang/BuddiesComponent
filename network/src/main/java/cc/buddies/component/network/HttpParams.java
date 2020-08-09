package cc.buddies.component.network;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Http请求参数转化处理
 * 生成RequestBody/FormBody/MultipartBody。
 */
public class HttpParams {

    private HttpParams() {
    }

    /**
     * 根据Map获取RequestBody
     * <p>表单提交方式
     *
     * @param params Map<String, Object>
     * @return FormBody请求体
     */
    public static FormBody getFormBody(@NonNull Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (String key : params.keySet()) {
            builder.add(key, String.valueOf(params.get(key)));
        }
        return builder.build();
    }

    /**
     * 根据Map获取RequestBody
     * <p>文件上传
     *
     * @param params Map<String, Object>
     * @return MultipartBody请求体
     */
    public static MultipartBody getMultipartBody(@NonNull Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, String.valueOf(params.get(key)));
        }
        return builder.build();
    }

    /**
     * 根据Map获取RequestBody
     * <p>使用@Body方式放入请求模型实体，则会默认使用application/json模式请求。
     * <p>还可以请求数据模型的方式请求数据，同样是json模式请求数据。
     *
     * @param params Map<String, Object>
     * @return RequestBody请求体
     */
    public static RequestBody getRequestBody(Map<String, Object> params) {
        String contentType = "application/json; charset=utf-8";
        MediaType mediaType = MediaType.parse(contentType);

        JSONObject jsonObject = new JSONObject(params);
        return RequestBody.create(mediaType, jsonObject.toString());
    }

}
