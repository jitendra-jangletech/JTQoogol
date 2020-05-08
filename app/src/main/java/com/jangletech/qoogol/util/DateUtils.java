package com.jangletech.qoogol.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String dd_MMMM_yyyy = "dd MMMM yyyy"; //08 July 2019

    public static final String dd_M_yyyy_hh_mm_ss = "dd-M-yyyy hh:mm:ss"; // 08-7-2019 08:51:58

    public static String getFormattedDate(String strDate){
        String formattedDate = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dd_MMMM_yyyy);
            formattedDate = simpleDateFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return formattedDate;
    }
}
