package com.jangletech.qoogol.model;

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

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.io.Serializable;

public class FAQModel implements Serializable {

    @SerializedName(Constant.faqt_name)
    private String faqt_name;

    @SerializedName(Constant.faqt_id)
    private String faqt_id;

    @SerializedName(Constant.faq_id)
    private String id;

    @SerializedName(Constant.faq_app)
    private String faq_app;

    @SerializedName(Constant.faq_question)
    private String question;

    @SerializedName(Constant.w_media_name)
    private String url;

    @SerializedName(Constant.md_from_type)
    private String fromType;

    @SerializedName(Constant.faq_answer)
    private String answer;

    public String getFaqt_name() {
        return faqt_name;
    }

    public void setFaqt_name(String faqt_name) {
        this.faqt_name = faqt_name;
    }

    public String getFaqt_id() {
        return faqt_id;
    }

    public void setFaqt_id(String faqt_id) {
        this.faqt_id = faqt_id;
    }

    public String getFaq_app() {
        return faq_app;
    }

    public void setFaq_app(String faq_app) {
        this.faq_app = faq_app;
    }


    private boolean expanded = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getAnswer() {
        return answer == null ? "" : answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "question='" + question + '\'' +
                ", url='" + url + '\'' +
                ", answer='" + answer + '\'' +
                ", expanded=" + expanded +
                '}';
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getFromType() {
        return fromType;
    }
}
