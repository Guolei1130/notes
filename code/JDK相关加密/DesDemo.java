package com.gl.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;


public class DesDemo {

    public static void main(String[] args) {
        String origin = "des加密原串";
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] encryptResult = desEncrypt(origin,secretKey);
            System.err.println(Util.byteArrayToHex(encryptResult));
            System.err.println(desDecrypt(encryptResult,secretKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance("DESede");
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] encryptResult = desedeEncrypt(origin,secretKey);
            System.err.println(Util.byteArrayToHex(encryptResult));
            System.err.println(desedeDecrypt(encryptResult,secretKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static byte[] desEncrypt(String string,Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "加密失败".getBytes();
    }

    private static String desDecrypt(byte[] bytes, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "解密失败";
    }

    private static byte[] desedeEncrypt(String string,Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "加密失败".getBytes();
    }

    private static String desedeDecrypt(byte[] bytes, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "解密失败";
    }
}
