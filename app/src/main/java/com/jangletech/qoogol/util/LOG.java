package com.jangletech.qoogol.util;

import android.util.Log;

import com.jangletech.qoogol.BuildConfig;

/////////////////////////////////////////////////////////////////////////////////////////////////
//
//            Copyright 2019 JANGLETECH SYSTEMS PRIVATE LIMITED. All rights reserved.
//
/////////////////////////////////////////////////////////////////////////////////////////////////

public class LOG {

    public static void e(String TAG, String message) {
        if (message != null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, message);
            }
        }
    }

    public static void d(String TAG, String message) {
        if (message != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, message);
            }
        }
    }

    public static void i(String TAG, String message) {
        if (message != null) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, message);
            }
        }
    }

    public static void w(String TAG, String message, Exception ex) {
        if (message != null) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, message, ex);
            }
        }
    }
}
