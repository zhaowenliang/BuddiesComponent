package cc.buddies.component.storage.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密处理
 * 推荐AES-256-CBC模式，String类型的key长度为32。
 * <pre>eg:
 *     byte[] content = "hello".getBytes();
 *     byte[] key = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes();
 *     byte[] iv = "bbbbbbbbbbbbbbbb".getBytes();
 *
 *     // 加密
 *     // 如果使用UTF-8编码byte[]为String，再将String解析为byte[]，数组长度和内容和原本不一致。
 *     // 原因是 UTF-8 是多字节编码，使用单字节编码 ISO-8859-1 编码可以解决。new String(encryptBytes, StandardCharsets.ISO_8859_1);
 *     byte[] encryptBytes = AESUtils.encryptCBC(content, key, iv);
 *     String encodeString = StringUtils.newStringUtf8(Base64.encodeBase64(encryptBytes));
 *
 *     // 解密
 *     byte[] decodeBase64 = Base64.decodeBase64(encodeString);
 *     byte[] decryptBytes = AESUtils.decryptCBC(decodeBase64, key, iv);
 *     String decryptString = StringUtils.newStringUtf8(decryptBytes);
 * </pre>
 * <pre>eg: 默认使用Base64编码和解析的方式
 *     String encryptStr = AESUtils.encryptBase64CBC(content, key, iv);
 *     String decryptStr = AESUtils.decryptBase64CBC(encryptStr, key, iv);
 * </pre>
 */
public class AESUtils {

    /**
     * 随机获取一个密钥长度为256位的AES密钥
     *
     * @param seed 随机种子
     * @return 长度为32的字节数组
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] getAESKey(byte[] seed) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom(seed);
        keyGen.init(256, secureRandom);   // 这里可以是 128、192、256、越大越安全
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 随机获取一个AES向量
     *
     * @param seed 随机种子
     * @return 长度为16的字节数组
     * @throws NoSuchPaddingException   异常
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] getAESIv(byte[] seed) throws NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom randomSecureRandom = new SecureRandom(seed);
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * AES加密（CBC方式），返回Base64转化后的字符串。
     *
     * @param rawSrc 原始数据
     * @param rawKey 加密key
     * @param rawIv  加密iv
     * @return 加密后内容，使用Base64将byte[]转化为String
     * @throws Exception 异常
     */
    public static String encryptBase64CBC(byte[] rawSrc, byte[] rawKey, byte[] rawIv) throws Exception {
        byte[] bytes = encryptCBC(rawSrc, rawKey, rawIv);
        return Base64.encodeBase64String(bytes);
    }

    /**
     * AES解密（CBC方式），返回Base64转化后的字符串。
     *
     * @param src    原始数据字符串（是使用Base64编码的字符串）
     * @param rawKey 加密key
     * @param rawIv  加密iv
     * @return 解密后内容，utf-8编码String
     * @throws Exception 异常
     */
    public static String decryptBase64CBC(String src, byte[] rawKey, byte[] rawIv) throws Exception {
        byte[] rawSrc = Base64.decodeBase64(src);

        byte[] decryptBytes = decryptCBC(rawSrc, rawKey, rawIv);
        return StringUtils.newStringUtf8(decryptBytes);
    }

    /**
     * AES加密（CBC方式）
     *
     * @param rawSrc 原始数据
     * @param rawKey 加密key
     * @param rawIv  加密iv
     * @return 加密后内容
     * @throws Exception 异常
     */
    public static byte[] encryptCBC(byte[] rawSrc, byte[] rawKey, byte[] rawIv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(rawIv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        return cipher.doFinal(rawSrc);
    }

    /**
     * AES解密（CBC方式）
     *
     * @param rawSrc 原始数据
     * @param rawKey 解密key
     * @param rawIv  解密iv
     * @return 解密后内容
     * @throws Exception 异常
     */
    public static byte[] decryptCBC(byte[] rawSrc, byte[] rawKey, byte[] rawIv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(rawKey, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(rawIv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
        return cipher.doFinal(rawSrc);
    }

}
