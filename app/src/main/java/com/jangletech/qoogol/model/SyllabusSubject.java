package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class SyllabusSubject {

    @SerializedName(Constant.sm_id)
    private String subjectId;
    @SerializedName(Constant.sm_sub_name)
    private String subjectName;

    public String getSubjectId() {
        return subjectId!=null?subjectId:"";
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName!=null?subjectName:"";
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
