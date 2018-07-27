package com.gl.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;


public class AESemo {
    public static void main(String[] args) {
        String origin = "aes加密原串";
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] encryptResult = aesEncrypt(origin,secretKey);
            System.err.println(Util.byteArrayToHex(encryptResult));
            System.err.println(aesDecrypt(encryptResult,secretKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] aesEncrypt(String string, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "加密失败".getBytes();
    }

    private static String aesDecrypt(byte[] bytes, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "解密失败";
    }
}
