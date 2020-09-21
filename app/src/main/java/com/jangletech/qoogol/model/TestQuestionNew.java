package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.io.Serializable;

public class TestQuestionNew implements Serializable {

    private boolean isAnsweredRight;

    @SerializedName(Constant.sub_ans_right_wrong)
    private boolean rightAnswered;

    public boolean isRightAnswered() {
        return rightAnswered;
    }

    public void setRightAnswered(boolean rightAnswered) {
        this.rightAnswered = rightAnswered;
    }

    public float getRightAnsProbability() {
        return rightAnsProbability;
    }

    public void setRightAnsProbability(float rightAnsProbability) {
        this.rightAnsProbability = rightAnsProbability;
    }

    @SerializedName(Constant.sub_ans_probability)
    private float rightAnsProbability;

    @SerializedName(Constant.ttqa_id)
    private int ttqa_id;

    @SerializedName(Constant.ttqa_obtain_marks)
    private String ttqa_obtain_marks;

    @SerializedName(Constant.ttqa_sub_ans)
    private String ttqa_sub_ans;

    @SerializedName(Constant.ttqa_mcq_ans_1)
    private boolean ttqa_mcq_ans_1;

    @SerializedName(Constant.ttqa_mcq_ans_2)
    private boolean ttqa_mcq_ans_2;

    @SerializedName(Constant.ttqa_mcq_ans_3)
    private boolean ttqa_mcq_ans_3;

    @SerializedName(Constant.ttqa_mcq_ans_4)
    private boolean ttqa_mcq_ans_4;

    @SerializedName(Constant.ttqa_mcq_ans_5)
    private boolean ttqa_mcq_ans_5;

    @SerializedName(Constant.ttqa_duration_taken)
    private String ttqa_duration_taken;

    @SerializedName(Constant.ttqa_md_id)
    private int ttqa_md_id;

    @SerializedName(Constant.ttqa_marked)
    private boolean ttqa_marked;

    @SerializedName(Constant.ttqa_saved)
    private boolean ttqa_saved;

    @SerializedName(Constant.ttqa_visited)
    private boolean ttqa_visited;

    @SerializedName(Constant.ttqa_attempted)
    private boolean ttqa_attempted;

    @SerializedName(Constant.q_media_type)
    private String q_media_type;

    @SerializedName(Constant.q_sm_id)
    private String q_sm_id;

    public boolean isQlc_like_flag() {
        return qlc_like_flag;
    }

    public void setQlc_like_flag(boolean qlc_like_flag) {
        this.qlc_like_flag = qlc_like_flag;
    }

    public boolean isQlc_fav_flag() {
        return qlc_fav_flag;
    }

    public void setQlc_fav_flag(boolean qlc_fav_flag) {
        this.qlc_fav_flag = qlc_fav_flag;
    }

    public String getQ_media_type() {
        return q_media_type;
    }

    public void setQ_media_type(String q_media_type) {
        this.q_media_type = q_media_type;
    }

    public String getQ_sm_id() {
        return q_sm_id;
    }

    public void setQ_sm_id(String q_sm_id) {
        this.q_sm_id = q_sm_id;
    }

    public String getQ_cm_id() {
        return q_cm_id;
    }

    public void setQ_cm_id(String q_cm_id) {
        this.q_cm_id = q_cm_id;
    }

    public String getQ_md_id() {
        return q_md_id;
    }

    public void setQ_md_id(String q_md_id) {
        this.q_md_id = q_md_id;
    }

    public String getQ_trending() {
        return q_trending;
    }

    public void setQ_trending(String q_trending) {
        this.q_trending = q_trending;
    }

    public String getQ_popular() {
        return q_popular;
    }

    public void setQ_popular(String q_popular) {
        this.q_popular = q_popular;
    }

    public String getQ_recent() {
        return q_recent;
    }

    public void setQ_recent(String q_recent) {
        this.q_recent = q_recent;
    }

    public String getQ_no_of_ratings() {
        return q_no_of_ratings;
    }

    public void setQ_no_of_ratings(String q_no_of_ratings) {
        this.q_no_of_ratings = q_no_of_ratings;
    }

    public String getQ_status() {
        return q_status;
    }

    public void setQ_status(String q_status) {
        this.q_status = q_status;
    }

    public String getQ_view_count() {
        return q_view_count;
    }

    public void setQ_view_count(String q_view_count) {
        this.q_view_count = q_view_count;
    }

    public String getQ_views() {
        return q_views;
    }

    public void setQ_views(String q_views) {
        this.q_views = q_views;
    }

    public String getQ_solved_by() {
        return q_solved_by;
    }

    public void setQ_solved_by(String q_solved_by) {
        this.q_solved_by = q_solved_by;
    }

    public boolean isAnsweredRight() {
        return isAnsweredRight;
    }

    public void setAnsweredRight(boolean answeredRight) {
        isAnsweredRight = answeredRight;
    }

    public String getRating() {
        if (rating == null) {
            return "0";
        } else {
            if (rating.length() > 3) {
                return rating.substring(0, 3);
            }
            return rating;
        }

    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAttended_by() {
        if (attended_by == null)
            return "0";
        else
            return attended_by;
    }

    public void setAttended_by(String attended_by) {
        this.attended_by = attended_by;
    }

    public String getQue_option_type() {
        return que_option_type;
    }

    public void setQue_option_type(String que_option_type) {
        this.que_option_type = que_option_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLikes() {
        return likes != null ? likes : "0";
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments != null ? comments : "0";
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShares() {
        return shares != null ? shares : "0";
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getA_mcq_2() {
        return a_mcq_2;
    }

    public void setA_mcq_2(String a_mcq_2) {
        this.a_mcq_2 = a_mcq_2;
    }

    public String getA_mcq_3() {
        return a_mcq_3;
    }

    public void setA_mcq_3(String a_mcq_3) {
        this.a_mcq_3 = a_mcq_3;
    }

    public String getA_mcq_4() {
        return a_mcq_4;
    }

    public void setA_mcq_4(String a_mcq_4) {
        this.a_mcq_4 = a_mcq_4;
    }

    public String getA_mcq_5() {
        return a_mcq_5;
    }

    public void setA_mcq_5(String a_mcq_5) {
        this.a_mcq_5 = a_mcq_5;
    }

    public String getQ_mcq_op_1() {
        return q_mcq_op_1 != null ? q_mcq_op_1 : "";
    }

    public void setQ_mcq_op_1(String q_mcq_op_1) {
        this.q_mcq_op_1 = q_mcq_op_1;
    }

    public String getQ_mcq_op_2() {
        return q_mcq_op_2 != null ? q_mcq_op_2 : "";
    }

    public void setQ_mcq_op_2(String q_mcq_op_2) {
        this.q_mcq_op_2 = q_mcq_op_2;
    }

    public String getQ_mcq_op_3() {
        return q_mcq_op_3 != null ? q_mcq_op_3 : "";
    }

    public void setQ_mcq_op_3(String q_mcq_op_3) {
        this.q_mcq_op_3 = q_mcq_op_3;
    }

    public String getQ_mcq_op_4() {
        return q_mcq_op_4 != null ? q_mcq_op_4 : "";
    }

    public void setQ_mcq_op_4(String q_mcq_op_4) {
        this.q_mcq_op_4 = q_mcq_op_4;
    }

    public String getQ_mcq_op_5() {
        if (q_mcq_op_5 == null)
            return "";
        else
            return q_mcq_op_5;
    }

    public void setQ_mcq_op_5(String q_mcq_op_5) {
        this.q_mcq_op_5 = q_mcq_op_5;
    }

    public String getQ_marks() {
        return q_marks;
    }

    public void setQ_marks(String q_marks) {
        this.q_marks = q_marks;
    }

    public boolean isTtqa_attempted() {
        return ttqa_attempted;
    }

    public void setTtqa_attempted(boolean ttqa_attempted) {
        this.ttqa_attempted = ttqa_attempted;
    }

    public boolean isTtqa_visited() {
        return ttqa_visited;
    }

    public void setTtqa_visited(boolean ttqa_visited) {
        this.ttqa_visited = ttqa_visited;
    }

    public boolean isTtqa_saved() {
        return ttqa_saved;
    }

    public void setTtqa_saved(boolean ttqa_saved) {
        this.ttqa_saved = ttqa_saved;
    }

    public boolean isTtqa_marked() {
        return ttqa_marked;
    }

    public void setTtqa_marked(boolean ttqa_marked) {
        this.ttqa_marked = ttqa_marked;
    }


    public boolean isTtqa_mcq_ans_5() {
        return ttqa_mcq_ans_5;
    }

    public void setTtqa_mcq_ans_5(boolean ttqa_mcq_ans_5) {
        this.ttqa_mcq_ans_5 = ttqa_mcq_ans_5;
    }

    public boolean isTtqa_mcq_ans_4() {
        return ttqa_mcq_ans_4;
    }

    public void setTtqa_mcq_ans_4(boolean ttqa_mcq_ans_4) {
        this.ttqa_mcq_ans_4 = ttqa_mcq_ans_4;
    }

    public boolean isTtqa_mcq_ans_3() {
        return ttqa_mcq_ans_3;
    }

    public void setTtqa_mcq_ans_3(boolean ttqa_mcq_ans_3) {
        this.ttqa_mcq_ans_3 = ttqa_mcq_ans_3;
    }

    public boolean isTtqa_mcq_ans_2() {
        return ttqa_mcq_ans_2;
    }

    public void setTtqa_mcq_ans_2(boolean ttqa_mcq_ans_2) {
        this.ttqa_mcq_ans_2 = ttqa_mcq_ans_2;
    }

    public boolean isTtqa_mcq_ans_1() {
        return ttqa_mcq_ans_1;
    }

    public void setTtqa_mcq_ans_1(boolean ttqa_mcq_ans_1) {
        this.ttqa_mcq_ans_1 = ttqa_mcq_ans_1;
    }

    public String getTtqa_sub_ans() {
        return ttqa_sub_ans != null ? ttqa_sub_ans : "";
    }

    public void setTtqa_sub_ans(String ttqa_sub_ans) {
        this.ttqa_sub_ans = ttqa_sub_ans;
    }

    public String getTtqa_obtain_marks() {
        return ttqa_obtain_marks;
    }

    public void setTtqa_obtain_marks(String ttqa_obtain_marks) {
        this.ttqa_obtain_marks = ttqa_obtain_marks;
    }


    public String getA_status() {
        return a_status;
    }

    public void setA_status(String a_status) {
        this.a_status = a_status;
    }

    public String getA_sub_ans() {
        return a_sub_ans != null ? a_sub_ans : "";
    }

    public void setA_sub_ans(String a_sub_ans) {
        this.a_sub_ans = a_sub_ans;
    }

    public String getTq_status() {
        return tq_status;
    }

    public void setTq_status(String tq_status) {
        this.tq_status = tq_status;
    }

    public String getTq_id() {
        return tq_id;
    }

    public void setTq_id(String tq_id) {
        this.tq_id = tq_id;
    }

    public int getTq_q_id() {
        return tq_q_id;
    }

    public void setTq_q_id(int tq_q_id) {
        this.tq_q_id = tq_q_id;
    }

    public double getTq_marks() {
        return tq_marks;
    }

    public void setTq_marks(double tq_marks) {
        this.tq_marks = tq_marks;
    }

    public int getTq_quest_seq_num() {
        return tq_quest_seq_num;
    }

    public void setTq_quest_seq_num(int tq_quest_seq_num) {
        this.tq_quest_seq_num = tq_quest_seq_num;
    }

    public boolean isTq_compulsory_quest() {
        return tq_compulsory_quest;
    }

    public void setTq_compulsory_quest(boolean tq_compulsory_quest) {
        this.tq_compulsory_quest = tq_compulsory_quest;
    }

    public String getCt_id() {
        return ct_id;
    }

    public void setCt_id(String ct_id) {
        this.ct_id = ct_id;
    }

    public String getTq_quest_catg() {
        return tq_quest_catg;
    }

    public void setTq_quest_catg(String tq_quest_catg) {
        this.tq_quest_catg = tq_quest_catg;
    }

    public String getTq_quest_disp_num() {
        return tq_quest_disp_num;
    }

    public void setTq_quest_disp_num(String tq_quest_disp_num) {
        this.tq_quest_disp_num = tq_quest_disp_num;
    }

    public String getTq_duration() {
        return tq_duration;
    }

    public void setTq_duration(String tq_duration) {
        this.tq_duration = tq_duration;
    }

    public String getQ_quest() {
        return q_quest;
    }

    public void setQ_quest(String q_quest) {
        this.q_quest = q_quest;
    }

    public String getQ_quest_desc() {
        return q_quest_desc;
    }

    public void setQ_quest_desc(String q_quest_desc) {
        this.q_quest_desc = q_quest_desc;
    }


    public String getQ_diff_level() {
        return q_diff_level;
    }

    public void setQ_diff_level(String q_diff_level) {
        this.q_diff_level = q_diff_level;
    }

    public String getQ_category() {
        return q_category;
    }

    public void setQ_category(String q_category) {
        this.q_category = q_category;
    }

    @SerializedName(Constant.q_cm_id)
    private String q_cm_id;

    @SerializedName(Constant.q_md_id)
    private String q_md_id;

    @SerializedName(Constant.q_trending)
    private String q_trending;

    @SerializedName(Constant.q_popular)
    private String q_popular;

    @SerializedName(Constant.q_recent)
    private String q_recent;

    @SerializedName(Constant.q_no_of_ratings)
    private String q_no_of_ratings;

    @SerializedName(Constant.q_status)
    private String q_status;

    @SerializedName(Constant.q_view_count)
    private String q_view_count;

    @SerializedName(Constant.q_views)
    private String q_views;

    @SerializedName(Constant.q_solved_by)
    private String q_solved_by;

    @SerializedName(Constant.qlc_like_flag)
    private boolean qlc_like_flag;

    @SerializedName(Constant.qlc_fav_flag)
    private boolean qlc_fav_flag;

    @SerializedName(Constant.q_avg_ratings)
    private String rating;

    @SerializedName(Constant.q_attempted_by)
    private String attended_by;

    @SerializedName(Constant.q_option_type)
    private String que_option_type;

    @SerializedName(Constant.q_type)
    private String type;

    @SerializedName(Constant.q_likes)
    private String likes;

    @SerializedName(Constant.q_comments)
    private String comments;

    @SerializedName(Constant.q_shares)
    private String shares;

    @SerializedName(Constant.a_mcq_2)
    private String a_mcq_2;

    @SerializedName(Constant.a_mcq_3)
    private String a_mcq_3;

    @SerializedName(Constant.a_mcq_4)
    private String a_mcq_4;

    @SerializedName(Constant.a_mcq_5)
    private String a_mcq_5;

    @SerializedName(Constant.q_mcq_op_1)
    private String q_mcq_op_1;

    @SerializedName(Constant.q_mcq_op_2)
    private String q_mcq_op_2;

    @SerializedName(Constant.q_mcq_op_3)
    private String q_mcq_op_3;

    @SerializedName(Constant.q_mcq_op_4)
    private String q_mcq_op_4;

    @SerializedName(Constant.q_mcq_op_5)
    private String q_mcq_op_5;

    @SerializedName(Constant.q_marks)
    private String q_marks;

    @SerializedName(Constant.a_status)
    private String a_status;

    @SerializedName(Constant.a_sub_ans)
    private String a_sub_ans;

    @SerializedName(Constant.tq_status)
    private String tq_status;

    @SerializedName(Constant.tq_id)
    private String tq_id;

    @SerializedName(Constant.tq_q_id)
    private int tq_q_id;

    @SerializedName(Constant.tq_marks)
    private double tq_marks;

    @SerializedName(Constant.tq_quest_seq_num)
    private int tq_quest_seq_num;

    @SerializedName(Constant.tq_compulsory_quest)
    private boolean tq_compulsory_quest;

    @SerializedName(Constant.ct_id)
    private String ct_id;

    @SerializedName(Constant.tq_quest_catg)
    private String tq_quest_catg;

    @SerializedName(Constant.tq_quest_disp_num)
    private String tq_quest_disp_num;

    @SerializedName(Constant.tq_duration)
    private String tq_duration;

    @SerializedName(Constant.q_quest)
    private String q_quest;

    @SerializedName(Constant.q_quest_desc)
    private String q_quest_desc;

    @SerializedName(Constant.q_diff_level)
    private String q_diff_level;

    @SerializedName(Constant.q_category)
    private String q_category;

    public int getTtqa_id() {
        return ttqa_id;
    }

    public void setTtqa_id(int ttqa_id) {
        this.ttqa_id = ttqa_id;
    }

    public String getTtqa_duration_taken() {
        return ttqa_duration_taken;
    }

    public void setTtqa_duration_taken(String ttqa_duration_taken) {
        this.ttqa_duration_taken = ttqa_duration_taken;
    }

    public int getTtqa_md_id() {
        return ttqa_md_id;
    }

    public void setTtqa_md_id(int ttqa_md_id) {
        this.ttqa_md_id = ttqa_md_id;
    }
}
