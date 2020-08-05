package cc.buddies.component.storage;

/*
 * 本组件核心功能为存储相关。
 * 1. 文件存储.
 * 2. 配置存储.
 * 3. SharePreferences存储.
 * 4. DB存储.
 *
 * io相关工具，使用commons-io库，不再单独实现。
 * {@link org.apache.commons.io.FileUtils}
 * {@link org.apache.commons.io.FilenameUtils}
 * {@link org.apache.commons.io.IOUtils}
 *
 * 消息摘要及加解密处理，使用commons-codec库。
 * {@link org.apache.commons.codec.digest.DigestUtils}
 * {@link org.apache.commons.codec.digest.Md5Crypt}
 * {@link org.apache.commons.codec.binary.Base64}
 *
 * 内存缓存可以使用
 * {@link android.util.LruCache}
 *
 * 磁盘缓存可以使用
 * {@link com.jakewharton.disklrucache.DiskLruCache}
 * 该工具来自于https://github.com/JakeWharton/DiskLruCache
 *
 * 数据缓存队列可是使用
 * 单向队列：{@link java.util.Queue}的子类
 * 双向队列 {@link java.util.Deque}的子类
 *
 *
 * 关键数据存储建议加密后存储，读取后再解密。
 *
 * 解析音视频信息 MediaMetadataRetriever
 * 解析图片信息 ExifInterface
 *
 */
