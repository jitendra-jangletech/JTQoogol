package com.jangletech.qoogol.model;

import java.util.List;

/**
 * Created by Pritali on 3/11/2020.
 */
public class UserSelectedExams {
    private String[] extraAttributeList;

    private String message;

    private int statusCode;

    private List<UserSelectedExamsData> object;

    public String[] getExtraAttributeList() {
        return extraAttributeList;
    }

    public void setExtraAttributeList(String[] extraAttributeList) {
        this.extraAttributeList = extraAttributeList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<UserSelectedExamsData> getObject() {
        return object;
    }

    public void setObject(List<UserSelectedExamsData> object) {
        this.object = object;
    }


}
