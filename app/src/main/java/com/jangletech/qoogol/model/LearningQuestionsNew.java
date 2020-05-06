package com.jangletech.qoogol.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;


/**
 * Created by Pritali on 4/24/2020.
 */
@Entity
public class LearningQuestionsNew {

    @PrimaryKey
    @NonNull
    @SerializedName(Constant.q_id)
    private String question_id;

    @SerializedName(Constant.q_quest)
    private String question;

    @SerializedName(Constant.q_quest_desc)
    private String questiondesc;

    @SerializedName(Constant.q_category)
    private String category;

    @SerializedName(Constant.sm_id)
    private String subject_id;

    @SerializedName(Constant.sm_sub_name)
    private String subject;

    @SerializedName(Constant.cm_id)
    private String chapter_id;

    @SerializedName(Constant.cm_chapter_name)
    private String chapter;

    @SerializedName(Constant.q_avg_ratings)
    private String rating;

    @SerializedName(Constant.q_diff_level)
    private String difficulty_level;

    @SerializedName(Constant.q_topic_id)
    private String topic;

    @SerializedName(Constant.q_cdatetime)
    private String posted_on;

    @SerializedName(Constant.q_last_used)
    private String lastused_on;

    @SerializedName(Constant.q_likes)
    private String likes;

    @SerializedName(Constant.q_comments)
    private String comments;

    @SerializedName(Constant.q_shares)
    private String shares;

    @SerializedName(Constant.q_duration)
    private String recommended_time;

    @SerializedName(Constant.a_ans_desc)
    private String answerDesc;

    @SerializedName(Constant.q_marks)
    private String marks;

    @SerializedName(Constant.q_mcq_op_1)
    private String mcq1;

    @SerializedName(Constant.q_mcq_op_2)
    private String mcq2;

    @SerializedName(Constant.q_mcq_op_3)
    private String mcq3;

    @SerializedName(Constant.q_mcq_op_4)
    private String mcq4;

    @SerializedName(Constant.q_mcq_op_5)
    private String mcq5;

    @SerializedName(Constant.q_attempted_by)
    private String attended_by;

    @SerializedName(Constant.q_solved_by)
    private String solve_right;

    @SerializedName(Constant.qlc_like_flag)
    private String is_liked;

    @SerializedName(Constant.qlc_fav_flag)
    private String is_fav;

    @SerializedName(Constant.q_type)
    private String type;

    @SerializedName(Constant.a_sub_ans)
    private String answer;

    @SerializedName(Constant.w_media_names)
    private String que_images;

    @SerializedName(Constant.q_media_type)
    private String que_media_typs;

    @SerializedName(Constant.q_option_type)
    private String que_option_type;

    @SerializedName(Constant.a_md_id)
    private String ans_mediaId;

    @SerializedName(Constant.w_ans_text)
    private String ans_media_names;

    @NonNull
    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(@NonNull String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestiondesc() {
        return questiondesc;
    }

    public void setQuestiondesc(String questiondesc) {
        this.questiondesc = questiondesc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDifficulty_level() {
        return difficulty_level;
    }

    public void setDifficulty_level(String difficulty_level) {
        this.difficulty_level = difficulty_level;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPosted_on() {
        return posted_on;
    }

    public void setPosted_on(String posted_on) {
        this.posted_on = posted_on;
    }

    public String getLastused_on() {
        return lastused_on;
    }

    public void setLastused_on(String lastused_on) {
        this.lastused_on = lastused_on;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getRecommended_time() {
        return recommended_time;
    }

    public void setRecommended_time(String recommended_time) {
        this.recommended_time = recommended_time;
    }

    public String getAnswerDesc() {
        return answerDesc;
    }

    public void setAnswerDesc(String answerDesc) {
        this.answerDesc = answerDesc;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getMcq1() {
        return mcq1;
    }

    public void setMcq1(String mcq1) {
        this.mcq1 = mcq1;
    }

    public String getMcq2() {
        return mcq2;
    }

    public void setMcq2(String mcq2) {
        this.mcq2 = mcq2;
    }

    public String getMcq3() {
        return mcq3;
    }

    public void setMcq3(String mcq3) {
        this.mcq3 = mcq3;
    }

    public String getMcq4() {
        return mcq4;
    }

    public void setMcq4(String mcq4) {
        this.mcq4 = mcq4;
    }

    public String getMcq5() {
        return mcq5;
    }

    public void setMcq5(String mcq5) {
        this.mcq5 = mcq5;
    }

    public String getAttended_by() {
        return attended_by;
    }

    public void setAttended_by(String attended_by) {
        this.attended_by = attended_by;
    }

    public String getSolve_right() {
        return solve_right;
    }

    public void setSolve_right(String solve_right) {
        this.solve_right = solve_right;
    }

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }



    public String getQue_media_typs() {
        return que_media_typs;
    }

    public void setQue_media_typs(String que_media_typs) {
        this.que_media_typs = que_media_typs;
    }

    public String getQue_option_type() {
        return que_option_type;
    }

    public void setQue_option_type(String que_option_type) {
        this.que_option_type = que_option_type;
    }


    public String getQue_images() {
        return que_images;
    }

    public void setQue_images(String que_images) {
        this.que_images = que_images;
    }

    public String getAns_mediaId() {
        return ans_mediaId;
    }

    public void setAns_mediaId(String ans_mediaId) {
        this.ans_mediaId = ans_mediaId;
    }

    public String getAns_media_names() {
        return ans_media_names;
    }

    public void setAns_media_names(String ans_media_names) {
        this.ans_media_names = ans_media_names;
    }
}
