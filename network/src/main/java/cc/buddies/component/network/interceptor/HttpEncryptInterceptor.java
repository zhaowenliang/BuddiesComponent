package cc.buddies.component.network.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import cc.buddies.component.network.HttpOptions;
import cc.buddies.component.network.constant.HttpEncryptType;
import cc.buddies.component.network.interfaces.HttpEncryptionInterface;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * Http请求加/解密
 * <p>对于Request中tag为HttpEncryptEnum的请求及响应进行加解密。
 */
public class HttpEncryptInterceptor implements Interceptor {

    private static final String TAG = "HttpEncryptInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        // 加密类型
        HttpEncryptType encryptType = getEncryptType(request);

        // 如果不是加密模式则不处理
        if (encryptType == null) {
            return chain.proceed(request);
        }

        // 请求加密
        Request encryptRequest = encryptRequest(request, encryptType);
        // 响应解密
        return encryptResponse(chain.proceed(encryptRequest), encryptType);
    }

    private Request encryptRequest(Request request, @NonNull HttpEncryptType encryptType) {
        Log.d(TAG, "encryptRequest()  HttpEncryptEnum: " + encryptType.getName());
        try {
            Request copy = request.newBuilder().build();
            RequestBody requestBody = copy.body();
            if (requestBody == null) return request;

            // 加密模式
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = getCharset(requestBody.contentType());
            String bodyJSON = buffer.readString(charset);

            // 加密处理
            String transModelJSON = dealEncrypt(encryptType, bodyJSON);
            RequestBody encryptRequestBody = createJSONRequestBody(transModelJSON);

            // 重新构建Request
            return copy.newBuilder().post(encryptRequestBody).build();
        } catch (Exception e) {
            e.printStackTrace();
            return request;
        }
    }

    private Response encryptResponse(Response response, @NonNull HttpEncryptType encryptType) {
        Log.d(TAG, "encryptResponse()  HttpEncryptEnum: " + encryptType.getName());
        Response clone = response.newBuilder().build();
        ResponseBody responseBody = clone.body();

        if (responseBody == null || !HttpHeaders.hasBody(clone)) {
            return response;
        }

        try {
            // 解密数据
            String responseModel = dealDecrypt(encryptType, responseBody.string());
            responseBody = ResponseBody.create(getMediaType(responseBody.contentType()), responseModel);
            return response.newBuilder().body(responseBody).build();
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }

    /**
     * 获取字符编码集
     * @param contentType MediaType
     * @return Charset字符编码集，默认为{@link StandardCharsets#UTF_8}
     */
    private Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
        if (charset == null) charset = StandardCharsets.UTF_8;
        return charset;
    }

    /**
     * 获取MediaType，如果参数为空，则获取一个默认MediaType。
     * @param contentType MediaType
     * @return MediaType 如果为空则获取一个默认MediaType。
     */
    private MediaType getMediaType(MediaType contentType) {
        return contentType != null ? contentType : MediaType.parse("application/json; charset=utf-8");
    }

    /**
     * 创建一个JSON类型RequestBody
     * @param json 请求数据
     * @return RequestBody
     */
    private RequestBody createJSONRequestBody(String json) {
        MediaType parse = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(parse, json);
    }

    /**
     * 获取加密类型
     * @param request 请求Request
     * @return {@link HttpEncryptType}
     */
    @Nullable
    private HttpEncryptType getEncryptType(@NonNull Request request) {
        return request.tag(HttpEncryptType.class);
    }

    /**
     * 加密处理
     * @param encryptType 加密类型
     * @param body 请求体
     * @return 加密数据
     */
    private String dealEncrypt(@NonNull HttpEncryptType encryptType, String body) {
        Map<String, HttpEncryptionInterface> encryption = HttpOptions.getInstance().getEncryption();
        String typeName = encryptType.getName();
        if (encryption.containsKey(typeName)) {
            HttpEncryptionInterface httpEncryption = encryption.get(typeName);
            assert httpEncryption != null;
            return httpEncryption.onEncrypt(body);
        }
        return body;
    }

    /**
     * 解密处理
     * @param encryptType 加密类型
     * @param body 响应体
     * @return 解密数据
     */
    private String dealDecrypt(@NonNull HttpEncryptType encryptType, String body) {
        Map<String, HttpEncryptionInterface> encryption = HttpOptions.getInstance().getEncryption();
        String typeName = encryptType.getName();
        if (encryption.containsKey(typeName)) {
            HttpEncryptionInterface httpEncryption = encryption.get(typeName);
            assert httpEncryption != null;
            return httpEncryption.onDecrypt(body);
        }
        return body;
    }

}
