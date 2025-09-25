package com.atsmart.mqtt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 * @author willwt
 * @date 2025/09/25 11:06
 */
public class CryptoUtil {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String HMAC_SHA_1 = "HmacSHA1";
    private static final String HMAC_MD_5 = "HmacMD5";


    public static String hmacSha256(String plainText, String key) throws Exception {
        return hmac(plainText, key, HMAC_SHA_256, "%064x");
    }

    // public static String hmacSha1(String plainText, String key) throws Exception {
    //     return hmac(plainText, key, HMAC_SHA_1, "%040x");
    // }

    // public static String hmacMd5(String plainText, String key) throws Exception {
    //     return hmac(plainText, key, HMAC_MD_5, "%032x");
    // }


    private static byte[] hmac(String plainText, String key, String algorithm) throws Exception {
        if (plainText == null || key == null) {
            throw new IllegalArgumentException("plainText or key is null");
        }

        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        mac.init(secretKeySpec);

        return mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    private static String hmac(String plainText, String key, String algorithm, String format) throws Exception {
        byte[] hmacResult = hmac(plainText, key, algorithm);
        BigInteger bigInteger = new BigInteger(1, hmacResult);
        return String.format(format, bigInteger);
    }

}
