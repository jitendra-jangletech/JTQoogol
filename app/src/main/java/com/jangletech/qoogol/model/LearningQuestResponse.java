package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 4/24/2020.
 */
public class LearningQuestResponse {


    private String Message;

    @SerializedName(Constant.row_count)
    private String row_count;

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.question_list)
    private List<LearningQuestionsNew> question_list;


    public String getRow_count() {
        return row_count;
    }

    public void setRow_count(String row_count) {
        this.row_count = row_count;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<LearningQuestionsNew> getQuestion_list() {
        return question_list;
    }

    public void setQuestion_list(List<LearningQuestionsNew> question_list) {
        this.question_list = question_list;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
