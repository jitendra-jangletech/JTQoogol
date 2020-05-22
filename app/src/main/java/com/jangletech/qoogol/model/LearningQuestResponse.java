package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 4/24/2020.
 */
public class LearningQuestResponse {


    private String Message;

    @SerializedName(Constant.prev_q_id)
    private String prev_q_id;

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.question_list)
    private List<LearningQuestionsNew> question_list;

    public String getPrev_q_id() {
        return prev_q_id;
    }

    public void setPrev_q_id(String prev_q_id) {
        this.prev_q_id = prev_q_id;
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
