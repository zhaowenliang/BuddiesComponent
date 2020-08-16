package cc.buddies.component.storage.io;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 封装文件操作工具
 */
public class FileUtils {

    /**
     * 创建目录
     *
     * @param fileDir 目录
     * @return true：创建成功或已经存在；false：创建失败。
     */
    public static boolean createDir(final File fileDir) {
        if (fileDir == null) return false;
        if (fileDir.exists()) return true;
        return fileDir.mkdirs();
    }

    /**
     * 创建文件
     *
     * @param file 文件
     * @return true：创建成功或已经存在；false：创建失败。
     */
    public static boolean createFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return true;

        try {
            final File parent = file.getParentFile();
            if (parent != null && parent.mkdirs()) {
                return file.createNewFile();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取目录下文件数量
     *
     * @param dir 文件目录
     * @return 文件数量
     */
    public static int getDirFileCount(String dir) {
        if (TextUtils.isEmpty(dir)) return 0;

        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) return 0;

        String[] list = file.list();
        return list == null ? 0 : list.length;
    }

    /**
     * 获取文件内容
     *
     * @param file File
     * @return String
     * @throws FileNotFoundException 文件未找到异常
     */
    public static String getFileString(File file) throws FileNotFoundException {
        return getFileString(file, -1);
    }

    /**
     * 获取文件内容
     *
     * @param file    File
     * @param maxSize 读取最大内容数
     * @return String
     * @throws FileNotFoundException 文件未找到异常
     */
    public static String getFileString(File file, long maxSize) throws FileNotFoundException {
        if (file == null || !file.exists()) throw new FileNotFoundException();
        return getFileString(new FileInputStream(file), maxSize);
    }

    /**
     * 获取文件内容
     *
     * @param inputStream 文件输入流
     * @param maxSize     读取内容最大数
     * @return String
     */
    public static String getFileString(InputStream inputStream, long maxSize) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        long size = 0;
        int index;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                index = stringBuilder.length();
                stringBuilder.append(line);
                stringBuilder.append("\n");

                // 计算字节长度，如果长度超出最大长度则截取。
                if (maxSize > 0) {
                    size += line.getBytes(StandardCharsets.UTF_8).length;
                    if (size > maxSize) {
                        stringBuilder.delete(index, stringBuilder.length());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
