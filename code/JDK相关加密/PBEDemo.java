package com.gl.security;



import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

public class PBEDemo {

    public static void main(String[] args) {
        String originString = "PBE加密原串";
        String yan = "yanaaaaa";
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            Key key = keyFactory.generateSecret(new PBEKeySpec(yan.toCharArray()));
            byte[] result = PBEEncrypt(originString,key);
            System.err.println(Util.byteArrayToHex(result));
            System.err.println(new String(PBEDecrypt(result,key)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] PBEEncrypt(String string, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE, key,new PBEParameterSpec("yanaaaaa".getBytes(),8));
            return cipher.doFinal(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1".getBytes();
    }

    private static byte[] PBEDecrypt(byte[] result, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.DECRYPT_MODE, key,new PBEParameterSpec("yanaaaaa".getBytes(),8));
            return cipher.doFinal(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1".getBytes();
    }

}
