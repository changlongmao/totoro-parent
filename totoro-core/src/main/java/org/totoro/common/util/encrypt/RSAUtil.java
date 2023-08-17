package org.totoro.common.util.encrypt;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Rsa公钥私钥加密工具类
 * @author ChangLF 2023-05-25
 */
public class RSAUtil {
    private static final String RSA_ALGORITHM = "RSA";

    /**
     * 生成公钥私钥对
     * @author ChangLF 2023/5/25 14:32
     * @return java.security.KeyPair
     **/
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取字符串格式的公钥
     * @param keyPair 公钥私钥对
     * @author ChangLF 2023/5/25 14:38
     * @return java.lang.String
     **/
    public static String getPublicKeyString(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取字符串格式的私钥
     * @param keyPair 公钥私钥对
     * @author ChangLF 2023/5/25 14:40
     * @return java.lang.String
     **/
    public static String getPrivateKeyString(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 公钥加密
     * @param data 要加密的数据
	 * @param publicKeyString 公钥
     * @author ChangLF 2023/5/25 14:32
     * @return java.lang.String 签名结果
     **/
    public static String encrypt(String data, String publicKeyString) throws Exception {
        PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString)));
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 私钥解密
     * @param encryptedData 要解密的数据
	 * @param privateKeyString 私钥
     * @author ChangLF 2023/5/25 14:32
     * @return java.lang.String
     **/
    public static String decrypt(String encryptedData, String privateKeyString) throws Exception {
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), StandardCharsets.UTF_8);
    }

    /**
     * 生成签名
     * @param data 原始数据
	 * @param privateKeyString 私钥
     * @author ChangLF 2023/5/25 14:33
     * @return java.lang.String 签名
     **/
    public static String sign(String data, String privateKeyString) throws Exception {
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 验证签名
     * @param data 原始数据
	 * @param signatureData 签名数据
	 * @param publicKeyString 公钥
     * @author ChangLF 2023/5/25 14:34
     * @return boolean 验证结果
     **/
    public static boolean verify(String data, String signatureData, String publicKeyString) throws Exception {
        PublicKey publicKey = KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString)));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signatureData));
    }

    public static void main(String[] args) throws Exception {
        // 生成密钥对
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String publicKeyString = getPublicKeyString(keyPair);
        String privateKeyString = getPrivateKeyString(keyPair);
        System.out.println("publicKeyString：   " + publicKeyString);
        System.out.println("privateKeyString：   " + privateKeyString);
//        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmxpztkqB5QDLTEnVv2Uv6cOphddxJP9ny0gfgH+enwGF+s2PtWZ6zvbN+WdVrgGMugybx4CrOY3aSuKi93J12gymVoLGS2ZUA/ZzzNeTvUGTJLvSc2/uS65TjgVPeQgO3570io9CLTB0iuz5fifHtYCXirgJiFlzapeRF8x/meyJ/S1zCifILKc89+PDeyqHTclXVpH26AIvK6GWmv2a4aDdit5536W/FG/ZJa/8BxCPlVNsz0OHtNmtiX7UWmg5x3MaFjCf+45OtWyCcv9HALfpCjMUhy3E0KL6mtyAp66jtoa26zWvTjXd2cf8aLoXrMF3e8l8crKuZjtrm+bKKQIDAQAB";
//        String privateKeyString = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCbGnO2SoHlAMtMSdW/ZS/pw6mF13Ek/2fLSB+Af56fAYX6zY+1ZnrO9s35Z1WuAYy6DJvHgKs5jdpK4qL3cnXaDKZWgsZLZlQD9nPM15O9QZMku9Jzb+5LrlOOBU95CA7fnvSKj0ItMHSK7Pl+J8e1gJeKuAmIWXNql5EXzH+Z7In9LXMKJ8gspzz348N7KodNyVdWkfboAi8roZaa/ZrhoN2K3nnfpb8Ub9klr/wHEI+VU2zPQ4e02a2JftRaaDnHcxoWMJ/7jk61bIJy/0cAt+kKMxSHLcTQovqa3ICnrqO2hrbrNa9ONd3Zx/xouheswXd7yXxysq5mO2ub5sopAgMBAAECggEBAJBDmU77nDgBdhZ+Mpb0Kg2XVS1NR0rvWH5mlg5yJXql8xVlb359VEr+mnSY3cOcY2WFZru4fQpqW5j9ljDsQJvQ2kJrHr9w7UJGUE0+Uodc39zx6fw/JvkzqznUzKe5jAQJGFFskFPx1uSFKEZj/8hdVBbNuOges1B4q5bkOedS+2HCCOVjYBC5OSYTVJMG6RBMOQKnX/cGzc/yNETu0bAyBj1dPrVTqf05vKmQe2pQvmcPrFjUKRDzfFjAT5eBtSF3VsEN3kYKMZmIOMKpq3pJudwqfXTl7B7Tm1yLu7rIXCp53b2NYl5z3D67Ub/1xlw0HyBGL87OLCBtIIqAKVECgYEA4MTi1lP/McMsQ+V/zFmJtmaNHsbTN3fvdgNE1nlQboOrk6fW6FVKW3lvrf83fj7R9SSeXTTmJakyyCCw7/pGnutwNaCUp9yMyw+ueeThbJHKxDx9piGepydQkg03rgBKnxeslWYG5LtAR2l2PAfzDxygTuTAkhC2gTDERDEvHf0CgYEAsKeH510CwUG7va/ksgp4F2GmiwGHmFDpl568Bgnzu9c3fqmgU9LgIq/v7cmXA8P39iy7HGjvxH1iqaN2cNBKeTpGf3sJbahyanJAMnbZO+uysjhKfOjAVgfVKz3TBKlY7Umrd6VDfNfEFURDJr+Q+DfKmPe0u4syVgKsZc4A3p0CgYAOpCSWi4ArEKiYf6GUgRycWg/FOnRVv6VEja/1PI/FsyQBCUTRe4TNcpqYvfAydAsfKdR7A9AeEehx6tc7upTRE0sK4zvrt00giRP3crZQaSrJH5ubr8Ly5ne0G9JNrk0uEc+3J585VQ052Z/PB0GI8XdrzDDTEu2germ+K2oDTQKBgQCsxHwe45FRLpeNUZxKIAEJZQB7jJhveBqspZrgERWlzTtCDmV19ZQteIIfvfuKvDsjMOYiR+LO+Y3nuK2acauUWlEeVleHPqn/vxNYB3I3rzFKKdF3zi5lNS3fJ4zcTz9gep9O2XtieqcT4XoHSG1gxTfIWOISVXV6Nw0iV9Hz5QKBgH+VLMD2OkRT9MR7bHUMOqaKJokxkGe5G4lhGA/b9/fohs49DaGWiVnh9eU27lkkE3NLI7ybDI8GNljJ2gVGmpBUT74+jS27nDPEmwOjmGoItMwiRWSD5KZKrrM1lL7KOJK1ojdVkO3UPDJr5HpPtVM5bjT9Ej8Wlt6JFYIzNj8A";
        // 要加密的数据
        String plainText = "Hello, RSA!";

        // 加密
        String encrypt = RSAUtil.encrypt(plainText, publicKeyString);
        System.out.println("encrypt Text: " + encrypt);

        // 解密
        String decryptedText = RSAUtil.decrypt(encrypt, privateKeyString);
        System.out.println("Decrypted Text: " + decryptedText);

        // 签名
        String signature = RSAUtil.sign(plainText, privateKeyString);
        System.out.println("Signature: " + signature);

        // 验证签名
        boolean isVerified = RSAUtil.verify(plainText, signature, publicKeyString);
        System.out.println("Signature Verified: " + isVerified);
    }
}
