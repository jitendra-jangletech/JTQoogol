package com.jangletech.qoogol.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jangletech.qoogol.model.AttemptedTest;
import com.jangletech.qoogol.model.QSet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayLisr(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public String fromAttemptedTestList(List<AttemptedTest> attemptedTests) {
        if (attemptedTests == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttemptedTest>>() {}.getType();
        String json = gson.toJson(attemptedTests, type);
        return json;
    }

    @TypeConverter
    public List<AttemptedTest> toAttemptedList(String attemptedTestString) {
        if (attemptedTestString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttemptedTest>>() {}.getType();
        List<AttemptedTest> attemptedTestList = gson.fromJson(attemptedTestString, type);
        return attemptedTestList;
    }

    @TypeConverter
    public String fromQSetList(List<QSet> qSetList) {
        if (qSetList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QSet>>() {}.getType();
        String json = gson.toJson(qSetList, type);
        return json;
    }

    @TypeConverter
    public List<QSet> toQSetList(String qSetString) {
        if (qSetString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QSet>>() {}.getType();
        List<QSet> qSetList = gson.fromJson(qSetString, type);
        return qSetList;
    }
}