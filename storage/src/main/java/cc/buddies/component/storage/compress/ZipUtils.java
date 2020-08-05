package cc.buddies.component.storage.compress;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩工具
 */
public class ZipUtils {

    /**
     * 压缩文件夹
     *
     * @param src     原始文件
     * @param target  压缩后文件
     * @param dirFlag zip文件中第一层是否包含一级目录（true包含；false不包含）
     * @throws IOException IO操作异常
     */
    public static void compress(@NonNull File src, @NonNull File target, boolean dirFlag) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(target))) {
            if (src.isDirectory()) {
                File[] listFiles = src.listFiles();

                for (File file : listFiles) {
                    if (dirFlag) {
                        recursionZip(zipOutputStream, file, src.getName() + File.separator);
                    } else {
                        recursionZip(zipOutputStream, file, "");
                    }
                }
            }
        }
    }

    /**
     * 递归压缩zip
     *
     * @param zipOut  zip输出流
     * @param file    文件
     * @param baseDir 当前压缩根目录
     * @throws IOException IO操作异常
     */
    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();

            if (listFiles == null || listFiles.length == 0) {
                zipOut.putNextEntry(new ZipEntry(baseDir + file.getName() + File.separator));
                zipOut.closeEntry();
            } else {
                for (File child : listFiles) {
                    recursionZip(zipOut, child, baseDir + file.getName() + File.separator);
                }
            }
        } else {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));

                int temp;
                byte[] buf = new byte[8192];
                while ((temp = inputStream.read(buf)) != -1) {
                    zipOut.write(buf, 0, temp);
                }
                zipOut.closeEntry();
            }
        }
    }

    /**
     * 解压缩
     *
     * @param src 压缩文件
     * @param out 输出路径
     * @throws IOException IO操作异常
     */
    public static void decompress(@NonNull File src, @NonNull File out) throws IOException {
        try (ZipFile zipFile = new ZipFile(src)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File outFile = new File(out, entry.getName());

                // 防止解压文件带有"../"而将文件解压到对应目录以外，造成安全漏洞。
                if (entry.getName() != null && entry.getName().contains("../")) {
                    continue;
                }

                if (entry.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.mkdirs();
                    continue;
                }

                if (!outFile.getParentFile().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.getParentFile().mkdirs();
                }

                if (!outFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.createNewFile();
                }

                try (InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
                     OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile))) {
                    int temp;
                    byte[] buf = new byte[8192];
                    while ((temp = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, temp);
                    }
                }
            }
        }
    }

    /**
     * 解压assets目录下压缩包
     * @param context Context
     * @param assetName assets目录下的压缩包名称
     * @param outputDirectory 解压后输出路径
     * @throws IOException 解压异常
     */
    public static void decompressFromAssets(@NonNull Context context, @NonNull String assetName, @NonNull File outputDirectory) throws IOException {
        // 打开压缩文件
        InputStream inputStream = context.getAssets().open(assetName);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File outFile = new File(outputDirectory, entry.getName());

                // 防止解压文件带有"../"而将文件解压到对应目录以外，造成安全漏洞。
                if (entry.getName() != null && entry.getName().contains("../")) {
                    continue;
                }

                if (entry.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.mkdirs();
                    continue;
                }

                if (!outFile.getParentFile().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.getParentFile().mkdirs();
                }

                if (!outFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.createNewFile();
                }

                try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile))) {
                    int temp;
                    byte[] buf = new byte[8192];
                    while ((temp = zipInputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, temp);
                    }
                }
            }
        }
    }

}
