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

    public String getRatingsFilter(){
        return sharedPreferences.getString("rating","");
    }

    public String getOptionTypeFilter(){
        return sharedPreferences.getString("option_type","");
    }

    public String getQuetionTypeFilter(){
        return sharedPreferences.getString("que_type","");
    }

    public String getQueDiffLevelFilter(){
        return sharedPreferences.getString("Diff_level","");
    }

    public String getQueTrandingFilter(){
        return sharedPreferences.getString("qtranding","");
    }

    public String getQuePopularFilter(){
        return sharedPreferences.getString("qpopular","");
    }

    public String getQueRecentFilter(){
        return sharedPreferences.getString("qrecent","");
    }


    public void setQueTrendingFilter(String ratings) {
        sharedPreferences.edit()
                .putString("qtranding",ratings)
                .apply();
    }

    public void setQuePopularFilter(String ratings) {
        sharedPreferences.edit()
                .putString("qpopular",ratings)
                .apply();
    }

    public void setQueRecentFilter(String ratings) {
        sharedPreferences.edit()
                .putString("qrecent",ratings)
                .apply();
    }

    public void setRatingsFilter(String ratings) {
        sharedPreferences.edit()
                .putString("rating",ratings)
                .apply();
    }

    public void setQueDiffLevelFilter(String level) {
        sharedPreferences.edit()
                .putString("Diff_level",level)
                .apply();
    }



    public void setQueTypeFilter(String type) {
        sharedPreferences.edit()
                .putString("que_type",type)
                .apply();
    }

    public void setOptionTypeFilter(String type) {
        sharedPreferences.edit()
                .putString("option_type",type)
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
