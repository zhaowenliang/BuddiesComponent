package cc.buddies.component.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import cc.buddies.component.storage.io.PropertiesUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();

        assertEquals("cc.buddies.component.storage.test", appContext.getPackageName());
    }

    private Context getContext() {
        return ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testStorage() {
        Context appContext = getContext();

        File externalStorageDirectory = StorageUtils.getExternalStorageDirectory();
        Log.d("aaaa", "testStorage() externalStorageDirectory: " + externalStorageDirectory);

        File externalCacheDir = StorageUtils.getExternalCacheDir(appContext);
        File externalFileDir = StorageUtils.getExternalFileDir(appContext, null);

        Log.d("aaaa", "testStorage() externalCacheDir: " + externalCacheDir);
        Log.d("aaaa", "testStorage() externalFileDir: " + externalFileDir);

        File cacheDir = StorageUtils.getCacheDir(appContext);
        File filesDir = StorageUtils.getFilesDir(appContext);

        Log.d("aaaa", "testStorage() cacheDir: " + cacheDir);
        Log.d("aaaa", "testStorage() filesDir: " + filesDir);

        File downloadCacheDirectory = StorageUtils.getDownloadCacheDirectory();
        Log.d("aaaa", "testStorage() downloadCacheDirectory: " + downloadCacheDirectory);
    }

    @Test
    public void testLoadProperties() {
        Context appContext = getContext();

        String filename = "config.properties";
        File externalFileDir = StorageUtils.getExternalFileDir(appContext, Environment.DIRECTORY_DOCUMENTS);
        File file = new File(externalFileDir, filename);
        Properties load = PropertiesUtils.load(file);

        if (load != null) {
            Enumeration<?> enumeration = load.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                Log.d("aaaa", "properties key: " + key + "    value: " + load.getProperty(key, ""));
            }
        } else {
            Log.d("aaaa", "未找到" + filename);
        }
    }

    @Test
    public void testWriteProperties() {
        Context appContext = getContext();

        String filename = "config.properties";
        File externalFileDir = StorageUtils.getExternalFileDir(appContext, Environment.DIRECTORY_DOCUMENTS);
        File file = new File(externalFileDir, filename);

        Map<String, Object> data = new HashMap<>();
        data.put("aaa", "Hello");
        data.put("bbb", 100);

        boolean write = PropertiesUtils.write(file, data, null);
        Log.d("aaaa", "properties 写入数据" + (write ? "成功" : "失败"));
    }

}
