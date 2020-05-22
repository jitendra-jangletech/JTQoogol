package com.jangletech.qoogol.util;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String TAG = "DateUtils";
    public static final String dd_MMMM_yyyy = "dd-MMM-yyyy"; //08 Jul 2019

    public static final String dd_M_yyyy_hh_mm_ss = "dd-M-yyyy hh:mm:ss"; // 08-7-2019 08:51:58

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
}
