package cc.buddies.component.storage;

/**
 * 存储类型
 * <pre>
 *     在此定义公共存储类型image/audio/video/temp...
 *     如果是特定业务自定类型svideo/covers...在当前业务模块下自定义。
 *     自定义类型要做好cache/files区分， cache目录下内容在清除缓存的时候会被清除。
 * </pre>
 */
public class StorageType {

    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";

    // 下载数据/压缩包
    public static final String DATA = "data";

    // 日志
    public static final String LOG = "log";

    // 缓存
    public static final String TEMP = "temp";

}
