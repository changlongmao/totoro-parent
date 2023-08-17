package org.totoro.common.util.encrypt;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * AES加密算法工具类，可逆算法：加密、解密
 * AES/ECB/PKCS5Padding
 */
@Slf4j
public class AESUtils {

    private static final String KEY_ALGORITHM = "AES";

    /**
     * 默认的加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 获取密钥
     *
     * @param mode     加解密模式
     * @param password 密码
     * @return javax.crypto.Cipher
     * @author ChangLF 2023/8/16 11:37
     **/
    private static Cipher getCipher(int mode, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec sks = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        // 初始化为解密模式的密码器
        cipher.init(mode, sks);
        return cipher;
    }

    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码
     * @return base64密文（密文的长度随着待加密字符串的长度变化而变化，至少32位）
     */
    public static String encrypt(String content, String password) {
        String base64String = "";
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
            // 将待加密字符串转byte[]
            byte[] clearTextBytes = content.getBytes(StandardCharsets.UTF_8);
            // 加密结果
            byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);
            // byte[]转base64
            base64String = Base64.getEncoder().encodeToString(cipherTextBytes);
        } catch (Exception e) {
            log.warn("加密失败", e);
        }
        return base64String;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param base64String base64密文
     * @param password     加密时的密码
     * @return 明文
     */
    public static String decrypt(String base64String, String password) {
        String clearText = "";
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
            // base64转byte[]
            byte[] cipherTextBytes = Base64.getDecoder().decode(base64String);
            // 解密结果
            byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);
            // byte[]-->String
            clearText = new String(clearTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("解密失败", e);
        }
        return clearText;
    }
}