package com.jangletech.qoogol.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jangletech.qoogol.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class AppUtils {
    private static final String TAG = "AppUtils";
    public static final String NOT_CONNECTED = "NOT_CONNECTED";
    public static final String CONNECTED = "CONNECTED";

    public static void saveHashMap(HashMap<String, String> inputMap, Context mContext) {
        String converted = new Gson().toJson(inputMap);
        new PreferenceManager(mContext).saveString("FILTER",converted);
        Log.d(TAG, "Converted String : "+converted);
    }

    public static HashMap<String, String> loadHashMap(Context mContext) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> newMap = new HashMap<String, String>();
        newMap = (HashMap<String, String>) gson.fromJson(new PreferenceManager(mContext).getString("FILTER"), newMap.getClass());
        Log.d(TAG, "Saved Hashmaps : " + newMap);
        return newMap;
    }

    public static String getDateFormat(String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date date = format.parse(strDate);
        return date.toString();
    }

    public static boolean isEnteredDOBValid(int year, int monthOfYear, int dayOfMonth) {
        return getAgeInYears(year, monthOfYear, dayOfMonth) >= 13;
    }

    public static int getAgeInYears(int year, int monthOfYear, int dayOfMonth) {
        LocalDate dateOfBirth = new LocalDate(year, monthOfYear, dayOfMonth);
        LocalDate currentDate = new LocalDate();

        Period period = Period.fieldDifference(dateOfBirth, currentDate);
        Log.i(TAG, "period in years: " + period.getYears());
        return period.getYears();
    }

    public static String getUserId() {
        return String.valueOf(new PreferenceManager(QoogolApp.getInstance()).getInt(Constant.USER_ID));
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(QoogolApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String decodedString(String encodedString) {
        try {
            byte[] messageBytes = Base64.decode(encodedString, Base64.DEFAULT);
            return StringEscapeUtils.unescapeJava(new String(messageBytes, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String encodedString(String plainText) {
        String encodedString = "";
        if (plainText != null) {
            String encoded = Base64.encodeToString(plainText.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            encodedString = StringUtils.stripAccents(encoded);
        }
        return encodedString;
    }

    public static String getStringField(Object object) {
        return object != null ? String.valueOf(object) : "";
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void bounceAnim(Context context, View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.1, 20);
        anim.setInterpolator(interpolator);
        view.startAnimation(anim);
    }


    public static void apiCallFailureDialog(Activity activity, Throwable t) {
        if (t instanceof UnknownHostException) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Check your internet connection.")
                    .setPositiveButton("OK", null)
                    .show();

        }
    }
}
