package cc.buddies.component.network;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cc.buddies.component.network.interfaces.HttpEncryptionInterface;

/**
 * Http配置项。
 * 至少需配置BASE_URL。
 */
public class HttpOptions {

    // 加密/解密处理集合
    private final Map<String, HttpEncryptionInterface> ENCRYPTION_INTERFACES = new HashMap<>();

    private HttpOptions() {
    }

    public static HttpOptions getInstance() {
        return HttpOptions.SingleTonHolder.OPTIONS;
    }

    private static class SingleTonHolder {
        private static final HttpOptions OPTIONS = new HttpOptions();
    }

    /**
     * 是否包含HttpEncryptEnum类型的加解密方式
     *
     * @param encryptType 加解密类型
     * @return boolean
     */
    public boolean containEncryption(String encryptType) {
        return ENCRYPTION_INTERFACES.containsKey(encryptType);
    }

    /**
     * 添加一种加解密方式（为了防止覆盖重复的key值，使用前可以先使用containEncryption方法判断是否已经包含指定type）
     *
     * @param encryptType 加解密类型
     * @param encryption  加解密处理
     */
    public void addEncryption(@NonNull String encryptType, @NonNull HttpEncryptionInterface encryption) {
        ENCRYPTION_INTERFACES.put(encryptType, encryption);
    }

    /**
     * 移除一种加解密方式
     *
     * @param encryptType 加解密类型
     */
    public void removeEncryption(String encryptType) {
        ENCRYPTION_INTERFACES.remove(encryptType);
    }

    /**
     * 获取所有加解密方式
     *
     * @return Map<HttpEncryptEnum, HttpEncryptionInterface>
     */
    public Map<String, HttpEncryptionInterface> getEncryption() {
        return ENCRYPTION_INTERFACES;
    }

    /**
     * 清除所有加解密方式
     */
    public void clearEncryption() {
        ENCRYPTION_INTERFACES.clear();
    }

}
