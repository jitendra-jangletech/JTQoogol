package com.jangletech.qoogol.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.jangletech.qoogol.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AppUtils {
    private static final String TAG = "AppUtils";
    public static final String NOT_CONNECTED = "NOT_CONNECTED";
    public static final String CONNECTED = "CONNECTED";

    public static String getDateFormat(String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date date = format.parse(strDate);
        return date.toString();
    }

    public static String getUserId() {
        return String.valueOf(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID));
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
