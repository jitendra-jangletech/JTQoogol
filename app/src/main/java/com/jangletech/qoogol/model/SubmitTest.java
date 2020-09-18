package com.jangletech.qoogol.model;
import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class SubmitTest {

    @SerializedName(Constant.tt_duration_taken)
    private String tt_duration_taken;

    public String getTt_duration_taken() {
        return tt_duration_taken!=null?tt_duration_taken:"";
    }

    public void setTt_duration_taken(String tt_duration_taken) {
        this.tt_duration_taken = tt_duration_taken;
    }

    @SerializedName(Constant.tt_id)
    private String tt_id;

    @SerializedName(Constant.tm_id)
    private String tm_id;

    @SerializedName("List1")
    private List<TestQuestionNew> testQuestionNewList;

    public List<TestQuestionNew> getTestQuestionNewList() {
        return testQuestionNewList;
    }

    public void setTestQuestionNewList(List<TestQuestionNew> testQuestionNewList) {
        this.testQuestionNewList = testQuestionNewList;
    }

    public String getTt_id() {
        return tt_id;
    }

    public void setTt_id(String tt_id) {
        this.tt_id = tt_id;
    }

    public String getTm_id() {
        return tm_id;
    }

    public void setTm_id(String tm_id) {
        this.tm_id = tm_id;
    }
}
