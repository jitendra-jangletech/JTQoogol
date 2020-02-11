package com.jangletech.qoogol.util;

import android.content.Context;
import android.content.SharedPreferences;


/////////////////////////////////////////////////////////////////////////////////////////////////
//
//            Copyright 2019 JANGLETECH SYSTEMS PRIVATE LIMITED. All rights reserved.
//
/////////////////////////////////////////////////////////////////////////////////////////////////

public class PreferenceManager {

    public Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void resetSetting() {
        sharedPreferences.edit()
                .putString(Constant.user_id, "")
                .putBoolean(Constant.IS_LOGGED_IN, false)
                .apply();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit()
                .putBoolean(Constant.IS_LOGGED_IN, isLoggedIn)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constant.IS_LOGGED_IN, false);
    }

    public void saveUserId(String userId) {
        sharedPreferences.edit()
                .putString(Constant.user_id, userId)
                .apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(Constant.user_id, "");
    }

}
