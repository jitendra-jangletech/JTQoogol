package com.jangletech.qoogol.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

/////////////////////////////////////////////////////////////////////////////////////////////////
//
//            Copyright 2019 JANGLETECH SYSTEMS PRIVATE LIMITED. All rights reserved.
//
/////////////////////////////////////////////////////////////////////////////////////////////////

public class PreferenceManager {

    public Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context ctx) {
        if(ctx!=null)
        sharedPreferences = ctx.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setChapterFilter(Set chapterFilter) {
        sharedPreferences.edit()
                .putStringSet("chapter",chapterFilter)
                .apply();
    }

    public Set<String> getRatingsFilter(){
        return sharedPreferences.getStringSet("rating",new HashSet<>());
    }

    public void setRatingsFilter(Set subject) {
        sharedPreferences.edit()
                .putStringSet("rating",subject)
                .apply();
    }

    public void saveToken(String token) {
        sharedPreferences.edit()
                .putString(Constant.u_fcm_token, token)
                .apply();
    }
    public String getToken() {
        return sharedPreferences.getString(Constant.u_fcm_token, "");
    }

    public String getString(String key) {
            //SharedPreferences sharedPref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(key, "");
    }

    public int getInt(String key) {
        //SharedPreferences sharedPref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }

    public void saveInt(String key,int value) {
        sharedPreferences.edit()
                .putInt(key,value)
                .apply();
    }

    public void saveString(String key,String value) {
         sharedPreferences.edit()
        .putString(key,value)
        .apply();
    }

    public void saveBoolean(String key,boolean value) {
        sharedPreferences.edit()
                .putBoolean(key,value)
                .apply();
    }

    public Set<String> getSubjectFilter(){
        return sharedPreferences.getStringSet("subject",new HashSet<>());
    }

    public void setSubjectFilter(Set subject) {
        sharedPreferences.edit()
                .putStringSet("subject",subject)
                .apply();
    }

    public Set<String> getChapterFilter(){
        return sharedPreferences.getStringSet("chapter",new HashSet<>());
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

    public void saveProfileFetchId(String userId) {
        sharedPreferences.edit()
                .putString(Constant.fetch_profile_id, userId)
                .apply();
    }

    public String getProfileFetchId() {
        return sharedPreferences.getString(Constant.fetch_profile_id, "");
    }

}
