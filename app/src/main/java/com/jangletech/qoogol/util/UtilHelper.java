package com.jangletech.qoogol.util;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.jangletech.qoogol.BuildConfig;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Pritali on 1/28/2020.
 */
public class UtilHelper {
    private static final String TAG = "UtilHelper";

    //Production
    private static final String PRODUCTION_BASE_API = "http://192.168.0.109:8080/Qoogol/";

    //Debug
    private static final String DEBUG_BASE_API = "http://192.168.0.109:8080/Qoogol/";

    private static final String SIGN_IN_API = "auth/signInNew";
    private static final String COUNTRY = "auth/countryList";
    private static final String STATE = "auth/statesForCountry";


    private static String getBaseApi() {
        if (BuildConfig.DEBUG) {
            return DEBUG_BASE_API;
        } else {
            return PRODUCTION_BASE_API;
        }
    }

    public static String signIn() {
        return getBaseApi() + SIGN_IN_API;
    }

    public static String getCountryList() {
        return getBaseApi() + COUNTRY;
    }

    public static String getStateList() {
        return getBaseApi() + STATE;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static Integer getKeyFromValue(Map<Integer, String> map, String name) {
        int selectedKey = -1;
        for (Map.Entry<Integer, String> e : map.entrySet()) {
            int key = e.getKey();
            String value = e.getValue();
            if (value.equals(name)) {
                selectedKey = key;
                break;
            }
        }
        return selectedKey;
    }

    public static String roundAvoid(String value) {
        double scale = Math.pow(10, 1);
        return String.valueOf(Math.round(Double.parseDouble(value) * scale) / scale);
    }

    public static String formatMarks(Float marks) {
        if (marks %1 ==0)
           return String.valueOf(Math.round(marks));
        else
            return String.valueOf(marks);
    }


    public static String getProfilePath (String userId, String endPath) {
        String paddedString = "0000000000".substring(userId.length());
        return Constant.PRODUCTION_BASE_FILE_API + paddedString  +  userId + "/" + endPath;
    }

    public static String getGroupProfilePath (String chatRoomId) {
        String paddedString = "G" + "0000000000".substring(String.valueOf(chatRoomId).length());
        String profilePath = Constant.PRODUCTION_BASE_FILE_API +  paddedString + chatRoomId + "/" + paddedString + chatRoomId + "0001.png";
        return profilePath;
    }

    public static String decodedMessage(String message) {
        try {
            byte[] messageBytes = Base64.decode(message, Base64.DEFAULT);
            return new String(messageBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return "";
    }

    public static String parseDate(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String date = "";
        try {
            date = format.parse(dtStart).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getAPIError(String response) {
        switch (response) {
            case "408":
                return Constant.DB_TIMEOUT_ERROR;
            case "500":
                return Constant.DB_NETWORK_ERROR;
            case "302":
                return Constant.GENERAL_ERROR;
            case "301":
                return Constant.App_ERROR;
            case "501":
                return Constant.MULTILOGIN_ERROR;
            default:
                return Constant.ERROR;
        }
    }
}
