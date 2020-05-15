package com.jangletech.qoogol.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.lm_id;
import static com.jangletech.qoogol.util.Constant.lm_name;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

@Entity(tableName = "language")
public class Language {

    @SerializedName("200")
    private String responseCode;

    @SerializedName("61")
    private List<Language> languageList;

    @SerializedName(lm_id)
    @ColumnInfo
    @PrimaryKey
    private int lang_id;

    @ColumnInfo
    @SerializedName(lm_name)
    private String languageName;

    public int getLang_id() {
        return lang_id;
    }

    public void setLang_id(int lang_id) {
        this.lang_id = lang_id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

}
