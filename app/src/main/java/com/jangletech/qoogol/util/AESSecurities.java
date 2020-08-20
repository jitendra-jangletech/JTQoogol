/*
 * /////////////////////////////////////////////////////////////////////////////////////////////////
 * //
 * //            © Copyright 2019 JangleTech Systems Private Limited, Thane, India
 * //
 * /////////////////////////////////////////////////////////////////////////////////////////////////
 */

package com.jangletech.qoogol.util;

import android.util.Base64;
import android.util.Log;

import com.jangletech.qoogol.BuildConfig;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESSecurities {

    private String SecretKey = BuildConfig.SECRET_KEY;
    private static AESSecurities instance = null;

    public static AESSecurities getInstance() {
        if (instance == null) {
            instance = new AESSecurities();
        }
        return instance;
    }

    public String decrypt(String text) {
        if (text == null || text.equalsIgnoreCase("X") || text.isEmpty()) {
            return "";
        } else {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //this parameters should not be changed
                byte[] keyBytes = new byte[16];
                byte[] b = SecretKey.getBytes(StandardCharsets.UTF_8);
                int len = b.length;
                if (len > keyBytes.length)
                    len = keyBytes.length;
                System.arraycopy(b, 0, keyBytes, 0, len);
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
                IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                byte[] results = new byte[text.length()];
                results = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
                Log.i("Data", new String(results, StandardCharsets.UTF_8));
                return new String(results, StandardCharsets.UTF_8); // it returns the result as a String
            } catch (Exception e) {
                Log.e("Error in Decryption", e.toString());
            }
            return "";
        }
    }

    public String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = SecretKey.getBytes(StandardCharsets.UTF_8);
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] results = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeToString(results, Base64.DEFAULT); // it returns the result as a String
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
