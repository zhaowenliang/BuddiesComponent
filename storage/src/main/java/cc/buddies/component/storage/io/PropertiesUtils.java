package cc.buddies.component.storage.io;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件工具
 */
public class PropertiesUtils {

    /**
     * 读取配置文件
     *
     * @param file 配置文件
     * @return Properties配置文件
     */
    @Nullable
    public static Properties load(File file) {
        if (file == null || !file.exists() || file.isDirectory()) return null;

        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

    /**
     * 写入配置文件，先加载已有配置，再写入新配置。
     *
     * @param outFile  输出文件
     * @param data     写入数据集合
     * @param comments 描述
     * @return true:写入成功; false:写入失败.
     */
    public static boolean write(File outFile, Map<String, Object> data, String comments) {
        if (outFile == null) return false;
        if (data == null || data.isEmpty()) return false;

        Properties properties = null;
        if (outFile.exists() && outFile.isFile()) {
            properties = load(outFile);
        }

        return write(properties, outFile, data, comments);
    }

    /**
     * 写入配置文件，直接输出到配置文件
     *
     * @param properties 配置文件
     * @param outFile    输出文件
     * @param data       写入数据集合
     * @param comments   描述
     * @return true:写入成功; false:写入失败.
     */
    public static boolean write(Properties properties, File outFile, Map<String, Object> data, String comments) {
        if (data == null || data.isEmpty()) return false;
        if (outFile == null || outFile.isDirectory()) return false;

        if (properties == null) {
            properties = new Properties();
        }

        if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            outFile.getParentFile().mkdirs();
        }

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            properties.setProperty(key, String.valueOf(value));
        }

        try (OutputStream outputStream = new FileOutputStream(outFile)) {
            properties.store(outputStream, comments);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
