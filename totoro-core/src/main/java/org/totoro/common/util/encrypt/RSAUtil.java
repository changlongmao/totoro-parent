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
//        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlVJaTBMbwVpl/PqSkLxXjulS8Fxgr4NxgrIMpbCFG7KT64R09bXuchoLuMDXpuU+xJ35cdgWo28DCsY96DtdQLhLGdcXNhtXLXczYJtg57wZiGZR2wFTQTTKMzJ0qkZobSRD8Kg0o1pyZfQhchQem/HkbVSW0gRs16YcntgMcYQlMPC/AmbE+p+Etk5BRQnzrHoQMjDN5dYCC2qH2M2RNnOS5eQkxoO9cSUFuPLbabhAsq04lkQ6JceDVUvAYteuSTGSdzmP/D93/rQcHBFxNvYMSNrB7FHV3UkyUsy/ryeN8uqtCw+LeYfL+Y+gDxzRDmS8Zlpf7RGMYmLDE0afTwIDAQAB";
//        String privateKeyString = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVUlpMExvBWmX8+pKQvFeO6VLwXGCvg3GCsgylsIUbspPrhHT1te5yGgu4wNem5T7Enflx2BajbwMKxj3oO11AuEsZ1xc2G1ctdzNgm2DnvBmIZlHbAVNBNMozMnSqRmhtJEPwqDSjWnJl9CFyFB6b8eRtVJbSBGzXphye2AxxhCUw8L8CZsT6n4S2TkFFCfOsehAyMM3l1gILaofYzZE2c5Ll5CTGg71xJQW48ttpuECyrTiWRDolx4NVS8Bi165JMZJ3OY/8P3f+tBwcEXE29gxI2sHsUdXdSTJSzL+vJ43y6q0LD4t5h8v5j6APHNEOZLxmWl/tEYxiYsMTRp9PAgMBAAECggEAB9L63EWcDLOZ6HmQuHjF9b0QFXwhrNCsX/bIZt4q+1qOGaclIcLYra3js1iXdC1K7q6AZZC3DvVAKY5drA+AQWXmuYHlu3YPSViXnpZFq57Vj6oc2+bj3+TOO4SHTGzICAAAd1m4+F5C5Ei4f+vB9tXj7D6kcHi+40XW9hPIAyKe7W+EDlKVVeTPOiiIj9t0/enqGzMLC/nOjY0C63OjHiqOUb3zsn5TOpW1Phfo10Dy5zENdJTU1/iLRPOQxuDbFAL59IlVbimurqKTaZcGw5nCNW1JM4UcVVmv1gSCocpOeG893+qlz8N1Nr1rzfhf9soP+66J0CCYYimxbKPisQKBgQDSD7vxCg9w7kZ7Ijz4UVcpoeCbjpMJG5eGUDoZuPNmV5Ym06JPOxEYkIAeBoR7lYo1eUmebk+mLdKpygAxPFOvdDwWXhorqx2qrmFxAaYJuAf9hCs9f2MM6swZgJRnLVczCHe3av3mV1rz5RzhSzQbK6cPjMOEmrX2OhBx2iCETQKBgQC1+hwXotxZOydN8R8/FRhC22rJZHZjgKzsy3knkR/nC8R8mVGuJlu0Kqz4HMz7QCRXQNjku2QlR1Jj8amo6UOWsoNthd4W+RTqqYnUsGDw6rAyUG1rnYPkpRsNNq+TOp/hDFDLC9PKOmIl5Na/tInjuItfMHlfhXe6VOVbopywCwKBgQCNdokkB5lkfSK788/JjsW7RMfFGpwMqyc9DbRC9Fn+VwfIeAe7gdS+jTmqfssjiw57eoXIixNan0taiq3censFEqsQiSjotaER+8OlXe3aZ7QB18ti6/HhlhIDLqNCZLdvp8kvnX8Fa1jWHTp4wrOBhMFJ6IsXNzVJb8r5UWNKlQKBgFNhAbYivOt1mxCvRi3VMISRnGAiU8xLjPACWsSyTffDAIhGh5JPqPzoANC+AX2taflAkSBcbApYn3uDosTt/DX0HRv1lkjYX6kVfT2igwhfv9ON3lTMmz4MGFT5lYDT/6VRy0L9bFDgZ1ANhgfYZMWeLN20jCgkMzOA6oYN/9T5AoGALRl/jyGZrnsGGwrefa5k1Jdt0eNBlOuxv9daf7HPsGBIqoEzPS+zzorA6F6GkfwA8CUExoxZkPAWx12NMX7bxMiSYep5i1UrJldcJH6gqhD/xOvkFhlfIXpmwZEpnRgYljupMq1b0q2b8XmL1preNtpESjt0XI7dEQ+ikrqe1eA=";
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
