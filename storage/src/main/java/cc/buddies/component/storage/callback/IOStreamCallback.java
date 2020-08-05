package cc.buddies.component.storage.callback;

import java.io.File;

/**
 * 通用的IO流操作进度回调
 */
public interface IOStreamCallback {

    void onProgress(long currentLength, long totalLength);

    void onFinish(File file);

    void onException(Exception e);

}
