package com.jangletech.qoogol.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {
    public static final String NOT_CONNECTED ="NOT_CONNECTED";
    public static final String CONNECTED ="CONNECTED";

    public static String getDateFormat(String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date date = format.parse(strDate);
        return date.toString();
    }
}
