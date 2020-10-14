package com.jangletech.qoogol.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class AppUtils {

    private static final String TAG = "AppUtils";
    public static final String NOT_CONNECTED = "NOT_CONNECTED";
    public static final String CONNECTED = "CONNECTED";

    public static TinyDB tinyDB;

    public static void saveHashMap(HashMap<String, String> inputMap, Context mContext) {
        String converted = new Gson().toJson(inputMap);
        new PreferenceManager(mContext).saveString(Constant.FILTER, converted);
        Log.d(TAG, "Converted String : " + converted);
    }

    public static void saveQueFilterHashMap(HashMap<String, String> inputMap, Context mContext) {
        PreferenceManager manager = new PreferenceManager(mContext);
        manager.setRatingsFilter(inputMap.get(Constant.q_avg_ratings));
        manager.setQueDiffLevelFilter(inputMap.get(Constant.q_diff_level));
        manager.setQueTypeFilter(inputMap.get(Constant.q_type));
        manager.setOptionTypeFilter(inputMap.get(Constant.q_option_type));
        manager.setQueTrendingFilter(inputMap.get(Constant.q_trending));
        manager.setQuePopularFilter(inputMap.get(Constant.q_popular));
        manager.setQueRecentFilter(inputMap.get(Constant.q_recent));
    }

    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(Context activity, Uri uri) {
        File storageDir = getDirectory(activity);
        String filename = new ImageOptimization(activity).getPath(activity, uri);
        return new File(storageDir, filename.substring(filename.lastIndexOf("/") + 1));
    }

    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(Context activity, String path) {
        File storageDir = getDirectory(activity);
        return new File(storageDir, path.substring(path.lastIndexOf("/") + 1));
    }

    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(Activity activity) {
        // Create an image file name
        // String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = System.currentTimeMillis() + "";
        File storageDir = getDirectory(activity);
        return new File(storageDir, mFileName + ".jpg");
    }

    public static HashMap<String, String> loadQueFilterHashMap(Context mContext) {
        HashMap<String, String> newMap = new HashMap<String, String>();
        PreferenceManager manager = new PreferenceManager(mContext);
        newMap.put(Constant.q_avg_ratings,manager.getRatingsFilter());
        newMap.put(Constant.q_diff_level,manager.getQueDiffLevelFilter());
        newMap.put(Constant.q_type,manager.getQuetionTypeFilter());
        newMap.put(Constant.q_option_type,manager.getOptionTypeFilter());
        newMap.put(Constant.q_trending,manager.getQueTrandingFilter());
        newMap.put(Constant.q_popular,manager.getQuePopularFilter());
        newMap.put(Constant.q_recent,manager.getQueRecentFilter());
        return newMap;
    }

    public static HashMap<String, String> loadHashMap(Context mContext) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> newMap = new HashMap<String, String>();
        newMap = (HashMap<String, String>) gson.fromJson(new PreferenceManager(mContext).getString(Constant.FILTER), newMap.getClass());
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

    public static void showToast(Context context,Throwable t ,String msg) {
        if(t instanceof UnknownHostException){
            Toast.makeText(context, "Check Your Internet Connection.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static String getMedialUrl(Activity activity,String mdtId,String path){
        Log.d(TAG, "getMedialUrl: "+TinyDB.getInstance(activity).getString(mdtId)+path);
        return TinyDB.getInstance(activity).getString(mdtId)+path;
    }

    @SuppressLint("SimpleDateFormat")
    public static File createStickerGifyFile(Activity activity, String extension) {
        String mFileName = System.currentTimeMillis() + "";
        File storageDir = getDirectory(activity);
        return new File(storageDir, mFileName + "." + extension);
    }

    public static byte[] readFully(InputStream input, File imageFile) throws IOException {
        FileOutputStream output = new FileOutputStream(imageFile);
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        output.write(byteArray);
        return byteArray;
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static File getDirectory(Context context) {
        File directory = context.getExternalFilesDir("Qoogol");
        if (directory != null) {
            String path = directory.getAbsolutePath();
            if (path.contains("/Android/")) {
                path = path.substring(0, path.indexOf("/Android/"));
                directory = new File(path, "Qoogol");
                if (!directory.exists()) {
                    if (directory.mkdir()) {
                        return directory;
                    }
                } else {
                    return directory;
                }
            }
        }
        Log.e(TAG, "error in creation of directory");
        return context.getExternalFilesDir("Qoogol");
    }

    public static String getType(String name) {
        if (name.contains(".doc") || name.contains(".docx")) {
            // Word document
            return "application/msword";
        } else if (name.contains(".pdf")) {
            // PDF file
            return "application/pdf";
        } else if (name.contains(".ppt") || name.contains(".pptx")) {
            // Powerpoint file
            return "application/vnd.ms-powerpoint";
        } else if (name.contains(".xls") || name.contains(".xlsx")) {
            // Excel file
            return "application/vnd.ms-excel";
        } else if (name.contains(".wav") || name.contains(".mp3") || name.contains(".mp4")
                || name.contains(".m4a") || name.contains(".mkv") || name.contains(".opus")) {
            // WAV audio file
            return "audio/x-wav";
        } else if (name.contains(".zip") || name.contains(".rar")) {
            // WAV audio file
            return "application/x-wav";
        } else if (name.contains(".rtf")) {
            // RTF file
            return "application/rtf";
        } else if (name.contains(".txt")) {
            // RTF file
            return "text/*";
        } else
            return "application/*";
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
