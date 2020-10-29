package com.jangletech.qoogol.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.jangletech.qoogol.BuildConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


/**
 * Created by Pritali on 1/28/2020.
 */
public class UtilHelper {
    private static final String TAG = "UtilHelper";

    //Production
    private static final String PRODUCTION_BASE_API = "http://192.168.0.109:8080/Qoogol/";
    public static final String PRODUCTION_BASE_FILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/";
    public static final String PRODUCTION_MALE_PROFILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/male.png";
    public static final String PRODUCTION_FEMALE_PROFILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/female.png";


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

    private static String getProfileApi() {
        if (BuildConfig.DEBUG) {
            return PRODUCTION_BASE_FILE_API;
        } else {
            return PRODUCTION_BASE_FILE_API;
        }
    }


    public static String getProfileImageUrl(String imageName) {
        return Constant.PRODUCTION_BASE_FILE_API + imageName;
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
        if (marks % 1 == 0)
            return String.valueOf(Math.round(marks));
        else
            return String.valueOf(marks);
    }

    public static boolean isImage(Uri media, Context context) {
        ContentResolver cR = context.getContentResolver();
        String content = cR.getType(media);
        if (content != null) {
            Log.d("#>content got ", content);
            if (content.contains("image")) {
                Log.d("#>detected ", "is image");
                return true;
            }
        } else {
            File file = new File(Objects.requireNonNull(media.getPath()));
            String[] okFileExtensions = new String[]{"jpg", "png", "jpeg", "bmp", "tif", "tiff", "emf", "icon", "wmf"};
            Log.d("#>file ext got ", file.getName().toLowerCase());
            for (String extension : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    Log.d("#>detected ", "is image");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAudio(Uri media, Context context) {
        ContentResolver cR = context.getContentResolver();
        String content = cR.getType(media);
        if (content != null) {
            Log.d("#>content got ", content);
            if (content.contains("audio")) {
                Log.d("#>detected ", "is audio");
                return true;
            }
        } else {
            File file = new File(Objects.requireNonNull(media.getPath()));
            String[] okFileExtensions = new String[]{"wav", "mp3", "mp4", "m4a", "mkv", "opus"};
            Log.d("#>file ext got ", file.getName().toLowerCase());
            for (String extension : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    Log.d("#>detected ", "is audio");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDoc(Uri media, Context context) {

        File file = new File(Objects.requireNonNull(media.getPath()));
        String[] okFileExtensions = new String[]{"doc", "docx", "pdf", "ppt", "pptx", "xls", "xlsx", "wav", "txt", "rtf", "HTML", "HTM", "ODT", "ODS",
                "docm", "xps"};
        Log.d("#>file ext got ", file.getName().toLowerCase());
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                Log.d("#>detected ", "is audio");
                return true;
            }
        }
        return false;
    }


    public static boolean isVideo(Uri media, Context context) {
        ContentResolver cR = context.getContentResolver();
        String content = cR.getType(media);
        if (content != null) {
            Log.d("#>content got ", content);
            if (content.contains("video")) {
                Log.d("#>detected ", "is video");
                return true;
            }
        } else {
            File file = new File(Objects.requireNonNull(media.getPath()));
            String[] okFileExtensions = new String[]{"mp4", "3GP", "avi", "wav", "wmv", "mpeg", "vob", "flv", "mkv", "mov", "divx", "xvid"};
            Log.d("#>file ext got ", file.getName().toLowerCase());
            for (String extension : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    Log.d("#>detected ", "is video");
                    return true;
                }
            }
        }
        return false;
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

    public static String getProfilePath(String userId, String endPath) {
        String paddedString = "0000000000".substring(userId.length());
        return Constant.PRODUCTION_BASE_FILE_API + paddedString + userId + "/" + endPath;
    }

    public static String getGroupProfilePath(String chatRoomId) {
        String paddedString = "G" + "0000000000".substring(String.valueOf(chatRoomId).length());
        String profilePath = Constant.PRODUCTION_BASE_FILE_API + paddedString + chatRoomId + "/" + paddedString + chatRoomId + "0001.png";
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
