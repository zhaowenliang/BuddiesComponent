package cc.buddies.component.storage;

import cc.buddies.component.storage.compress.ZipUtils;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        String root = "E://compress";
        String name = "html-dev-小视频";

        File file = new File(root, name + ".zip");

        try {
            // 解压缩
            File file1 = new File(root, name + "-1");
            ZipUtils.decompress(file, file1);

            // 压缩
            File file2 = new File(root, name + "-1.zip");
            ZipUtils.compress(file1, file2, false);


            // 再解压缩
            File file3 = new File(root, name + "-2");
            ZipUtils.decompress(file2, file3);

            // 再压缩
            File file4 = new File(root, name + "-2.zip");
            ZipUtils.compress(file3, file4, false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testZip() {
        String src = "E://compress/html-dev-小视频";
        String target = "E://compress/html-dev-小视频-compress.zip";

        try {
            File srcFile = new File(src);
            File targetFile = new File(target);

            ZipUtils.compress(srcFile, targetFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUnZip() {
        String filepath = "E://compress/html-dev-小视频-compress.zip";
        String outDir = "E://compress/html-dev-小视频-unzip";

        try {
            File srcFile = new File(filepath);
            File targetFile = new File(outDir);

            ZipUtils.decompress(srcFile, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMediaFile() {
        String filepath = "E://aa/bb/cc/123.jpg";

        // 123
        String baseName = FilenameUtils.getBaseName(filepath);
        System.out.println("testMediaFile() baseName: " + baseName);

        // jpg
        String extension = FilenameUtils.getExtension(filepath);
        System.out.println("testMediaFile() extension: " + extension);

        // E://aa/bb/cc/
        String fullPath = FilenameUtils.getFullPath(filepath);
        System.out.println("testMediaFile() fullPath: " + fullPath);

        // E://aa/bb/cc
        String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(filepath);
        System.out.println("testMediaFile() fullPathNoEndSeparator: " + fullPathNoEndSeparator);

        // 123.jpg
        String name = FilenameUtils.getName(filepath);
        System.out.println("testMediaFile() name: " + name);

        // /aa/bb/cc/
        String path = FilenameUtils.getPath(filepath);
        System.out.println("testMediaFile() path: " + path);

        // /aa/bb/cc
        String pathNoEndSeparator = FilenameUtils.getPathNoEndSeparator(filepath);
        System.out.println("testMediaFile() pathNoEndSeparator: " + pathNoEndSeparator);

        // E:/
        String prefix = FilenameUtils.getPrefix(filepath);
        System.out.println("testMediaFile() prefix: " + prefix);

        // 3
        int prefixLength = FilenameUtils.getPrefixLength(filepath);
        System.out.println("testMediaFile() prefixLength: " + prefixLength);
    }

}