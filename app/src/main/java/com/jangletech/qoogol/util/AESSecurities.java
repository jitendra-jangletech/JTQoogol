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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESSecurities {

    private static final String TAG = "AESSecurities";
    private String paddedString = "989753";
    //private String SecretKey = BuildConfig.SECRET_KEY;
    private static AESSecurities instance = null;

    public static AESSecurities getInstance() {
        if (instance == null) {
            instance = new AESSecurities();
        }
        return instance;
    }

    public static String getMasterKey(String deviceId) {
        Log.d(TAG, "deviceId: "+deviceId);
        String key1 = "";
        SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy HH:mm:ss a");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTime = DateTime.now(DateTimeZone.UTC).toDate();
        String dateString = format.format(dateTime);
        if (deviceId.length() >= 4)
            key1 = ("989753" + deviceId.substring(0, 4) + dateString.substring(0, 5));
        else
            key1 = ("989753" + dateString.substring(0, 5));
        return key1;
    }

    public String getDecryptAppConfigKey() {
        String key = "";
        String deviceId = AppUtils.getDeviceId();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String date = sdf.format(new Date());
        Log.d(TAG, "getDecryptAppConfigKey : " + date);
        if (deviceId.length() >= 4) {
            key = paddedString + deviceId.substring(0, 4) + date.substring(0, 5);
        } else {
            key = paddedString + date.substring(0, 9);
        }
        Log.d(TAG, "getDecryptAppConfigKey: " + key);
        return key;
    }

    /*public String decryptMasterKey(String key, String text) {
        SecretKey = key;
        return decrypt(text);
    }

    public String encrypt(String key, String text) {
        SecretKey = key;
        return encrypt(text);
    }
*/
    public String decrypt(String key, String text) {
        Log.d(TAG, "decrypt Key : " + key);
        if (text == null ||
                text.equalsIgnoreCase("X") ||
                text.isEmpty()) {
            return "";
        } else {
            //SecretKey = key;
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //this parameters should not be changed
                byte[] keyBytes = new byte[16];
                byte[] b = key.getBytes(StandardCharsets.UTF_8);
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

    public String encrypt(String key, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        } else {
            //SecretKey = key;
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                byte[] keyBytes = new byte[16];
                byte[] b = key.getBytes(StandardCharsets.UTF_8);
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
}
