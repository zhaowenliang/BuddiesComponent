package cc.buddies.component.network.constant;

/**
 * Http请求加密类型
 * <p>对于name的处理，推荐以模块开头命名，保证name不会冲突。
 */
public class HttpEncryptType {

    private String name;

    private HttpEncryptType(String name) {
        this.name = name;
    }

    public static HttpEncryptType newInstance(String name) {
        return new HttpEncryptType(name);
    }

    public String getName() {
        return name;
    }
}
