package com.jangletech.qoogol.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;


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

    @SerializedName(Constant.q_sm_id)
    private String subject;

    @SerializedName(Constant.q_cm_id)
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

}
