package org.mercury.im.common.core.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMac {

    public static final String HMAC_SHA1 = "HmacSHA1";

    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String hmac(String algorithm, String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String generateHmacSha256(String key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

//    private void test() throws Exception {
//        String key = "x41f5x1d2s";
//        String deviceId = "2_1_register_1705318936714";
//        System.out.println(HMac.hmac("HmacMD5", deviceId, key));
//        System.out.println(HMac.hmac("HmacSHA1", deviceId, key));
//        System.out.println(HMac.hmac("HmacSHA256", deviceId, key));
//    }

}
