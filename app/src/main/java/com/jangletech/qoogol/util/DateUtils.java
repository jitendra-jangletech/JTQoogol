package com.jangletech.qoogol.util;

import android.annotation.SuppressLint;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final String TAG = "DateUtils";
    public static final String dd_MMMM_yyyy = "dd-MMM-yyyy"; //08 Jul 2019

    public static final String dd_M_yyyy_hh_mm_ss = "dd-M-yyyy hh:mm:ss"; // 08-7-2019 08:51:58

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String strDate) {

        long time = getMillisFromDate(strDate);

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String localeDateOfBirthFormat(String strDate) throws ParseException {
        String formattedDate = "";
        SimpleDateFormat inputFormat;
        try {
            if (strDate.contains("T")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            } else {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            // SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = inputFormat.parse(strDate);
            assert date != null;
            formattedDate = outputFormat.format(date);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = inputFormat.parse(strDate);
            assert date != null;
            formattedDate = outputFormat.format(date);
        }
        return formattedDate;
    }

    public static String getFormattedDate(String strDate) {
        Log.d(TAG, "getFormattedDate: " + strDate);
        if (strDate != null && strDate.length() > 10) {
            strDate.substring(0, 10);
        }
        String formattedDate = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dd_MMMM_yyyy);
            formattedDate = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static long getMillisFromDate(String strDate) {
        long millis = 0;
        if (strDate != null) {
            //2020-06-24T07:39:00
            try {
                String myDate = "2020-06-24 07:39:00";//strDate.replace("T", " ");//"2014/10/29 18:10:45";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(myDate);
                millis = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return millis;
    }

    @SuppressLint("SimpleDateFormat")
    public static String localeDateFormat(String strDate) {
        String formattedDate = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            long dateTime = System.currentTimeMillis();
            Log.i(TAG, "Current date : " + dateTime);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm MMM dd");
            Date date = inputFormat.parse(strDate);
            inputFormat.setTimeZone(TimeZone.getDefault());
            assert date != null;
            long diffMillis = dateTime - date.getTime();
            int hours = Math.round(diffMillis / (1000 * 60 * 60));
            if (hours > 0) {
                if (hours >= 96) {
                    return outputFormat.format(date);
                } else if (hours >= 24) {
                    if (hours <= 48) {
                        return "1 days ago";
                    } else if (hours <= 72) {
                        return "2 days ago";
                    } else {
                        return "3 days ago";
                    }
                } else {
                    return String.format(Locale.ENGLISH, "%d hours ago", hours);
                }
            }
            int mins = Math.round((diffMillis / (1000 * 60)) % 60);
            if (mins >= 1 && mins < 60) {
                return String.format(Locale.ENGLISH, "%d minutes ago", mins);
            }
            int seconds = (int) ((diffMillis / 1000) % 60);
            if (seconds >= 1) {
                return String.format(Locale.ENGLISH, "%d seconds ago", seconds);
            } else {
                return "1 seconds ago";
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return formattedDate;
    }
}
