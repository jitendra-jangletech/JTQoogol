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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESSecurities {

    private static final String TAG = "AESSecurities";
    private String paddedString = "989753";
    private static AESSecurities instance = null;

    public static AESSecurities getInstance() {
        if (instance == null) {
            instance = new AESSecurities();
        }
        return instance;
    }

    private static int getmmddhh(Calendar calendar, int hr) {
        int month;
        month = calendar.get(Calendar.MONTH) + 1;
        if (month == 12) {
            month = 1;
        }
        return Integer.parseInt(String.format(Locale.ENGLISH, "%02d%02d%02d",
                month, calendar.get(Calendar.DATE), hr == 23 ? hr : calendar.get(Calendar.HOUR_OF_DAY)));
    }

    public static String getMasterKey(int algo, String in_imei_num, String dateTime) throws ParseException {
        String key1 = "";
        int mmddhh;
        String dateString = "";
        // SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy HH:mm:ss a");
        Log.i(TAG, "Date: " + dateTime);
        Date date = outputFormat.parse(dateTime);
        assert date != null;
        dateString = outputFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.MINUTE) == 59 || calendar.get(Calendar.MINUTE) == 00) {
            if ((calendar.get(Calendar.HOUR_OF_DAY) == 23 || calendar.get(Calendar.MINUTE) == 59) ||
                    (calendar.get(Calendar.HOUR_OF_DAY) == 00 && calendar.get(Calendar.MINUTE) == 00)) {
                mmddhh = 343567;
            } else {
                mmddhh = getmmddhh(calendar, 23);
            }
        } else {
            mmddhh = getmmddhh(calendar, 0);
        }
        Log.i(TAG, "mmddhh: " + mmddhh);
        //a = "8/27/2020 3:21:18 AM" or "08/27/2020 03:21:18 AM" or "8/1/2020 3:1:18 AM"
        switch (algo) {
            case 1:
                key1 = (989753 ^ mmddhh) + in_imei_num.substring(0, 4) + dateString.substring(0, 5);
                break;
            case 2:
                key1 = dateString.substring(0, 9) + (452683 >> mmddhh);
                break;
            case 3:
                key1 = (813476 ^ mmddhh) + dateString.substring(0, 9);
                break;
            case 4:
                key1 = dateString.substring(2, 5) + (343466 >> mmddhh) + in_imei_num.substring(0, 4);
                break;
            case 5:
                key1 = dateString.substring(3, 5) + in_imei_num.substring(in_imei_num.length() - 4, 4) + (567835 << mmddhh);
                break;
            case 6:
                key1 = dateString.substring(0, 5) + (457874 ^ mmddhh) + in_imei_num.substring(0, 4);
                break;
            case 7:
                key1 = (743678 ^ mmddhh) + dateString.substring(2, 5) + in_imei_num.substring(0, 4);
                break;
            case 8:
                key1 = dateString.substring(0, 5) + in_imei_num.substring(0, 4) + (565477 << mmddhh);
                break;
            case 9:
                key1 = (546578 >> mmddhh) + dateString.substring(0, 9);
                break;
            default:
                key1 = (989753 ^ mmddhh) + in_imei_num.substring(0, 4) + dateString.substring(0, 5);
                break;
        }
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
