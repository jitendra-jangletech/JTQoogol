package com.jangletech.qoogol.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

@Entity(tableName = "nationality")
public class Nationality {

    @PrimaryKey
    @ColumnInfo
    @SerializedName(Constant.mdt_id)
    private int nationality_Id;

    @ColumnInfo
    @SerializedName(Constant.mdt_desc)
    private String nationality;

    public int getNationality_Id() {
        return nationality_Id;
    }

    public void setNationality_Id(int nationality_Id) {
        this.nationality_Id = nationality_Id;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
