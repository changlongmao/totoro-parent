package org.totoro.common.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密算法工具类
 *
 * @explain 可逆算法：加密、解密
 * AES/ECB/PKCS7Padding
 */

public class AESUtils {

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
            byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");
            // 将待加密字符串转byte[]
            byte[] clearTextBytes = content.getBytes(StandardCharsets.UTF_8);
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // 加密结果
            byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);

            // byte[]转base64
            base64String = Base64.getEncoder().encodeToString(cipherTextBytes);
        } catch (Exception e) {
            e.printStackTrace();
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
            byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化为解密模式的密码器
            cipher.init(Cipher.DECRYPT_MODE, sks);
            // base64转byte[]
            byte[] cipherTextBytes = Base64.getDecoder().decode(base64String);
            // 解密结果
            byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);
            // byte[]-->String
            clearText = new String(clearTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clearText;
    }
}