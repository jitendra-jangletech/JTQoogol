package com.jangletech.qoogol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.database.converter.Converters;
import com.jangletech.qoogol.util.Constant;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
public class TestModelNew implements Serializable {

    private String flag;
    private String userId;

    public String getTest_sections() {
        return test_sections;
    }

    public void setTest_sections(String test_sections) {
        this.test_sections = test_sections;
    }

    @SerializedName(Constant.test_sections)
    private String test_sections;

    @SerializedName(Constant.test_description)
    private String test_description;

    @NotNull
    @PrimaryKey
    @SerializedName(Constant.tm_id)
    private int tm_id;

    @SerializedName(Constant.tm_sm_id)
    private String tm_sm_id;

    @SerializedName(Constant.tm_up_u_id)
    private String tm_up_u_id;

    @SerializedName(Constant.tm_name)
    private String tm_name;

    @SerializedName(Constant.tm_diff_level)
    private String tm_diff_level;

    @SerializedName(Constant.tm_tot_marks)
    private String tm_tot_marks;

    @SerializedName(Constant.tm_type)
    private String tm_type;

    @SerializedName(Constant.tm_ranking)
    private String tm_ranking;

    @SerializedName(Constant.tm_rating_count)
    private String tm_rating_count;

    @SerializedName(Constant.tm_status)
    private String tm_status;

    @SerializedName(Constant.tm_neg_mks)
    private String tm_neg_mks;

    @SerializedName(Constant.tm_catg)
    private String tm_catg;

    @SerializedName(Constant.tm_category_1)
    private String tm_category_1;

    @SerializedName(Constant.tm_category_2)
    private String tm_category_2;

    @SerializedName(Constant.tm_category_3)
    private String tm_category_3;

    @SerializedName(Constant.tm_avg_rating)
    private String tm_avg_rating;

    @SerializedName(Constant.tm_sharable)
    private String tm_sharable;

    @SerializedName(Constant.tm_comp_quest_count)
    private String tm_comp_quest_count;

    @SerializedName(Constant.tm_duration)
    private String tm_duration;

    @SerializedName(Constant.tm_cm_id)
    private String tm_cm_id;

    @SerializedName(Constant.tm_visibility)
    private String tm_visibility;

    @SerializedName(Constant.tq_id)
    private String tq_id;

    @SerializedName(Constant.tq_tm_id)
    private String tq_tm_id;

    @SerializedName(Constant.tq_q_id)
    private String tq_q_id;

    @SerializedName(Constant.tq_marks)
    private String tq_marks;

    @SerializedName(Constant.tq_quest_seq_num)
    private String tq_quest_seq_num;

    @SerializedName(Constant.tq_compulsory_quest)
    private String tq_compulsory_quest;

    @SerializedName(Constant.tq_ref_id)
    private String tq_ref_id;

    @SerializedName(Constant.tq_quest_catg)
    private String tq_quest_catg;

    @SerializedName(Constant.tq_quest_disp_num)
    private String tq_quest_disp_num;

    @SerializedName(Constant.tq_duration)
    private String tq_duration;

    @SerializedName(Constant.tq_status)
    private String tq_status;

    @SerializedName(Constant.tt_id)
    private String tt_id;

    @SerializedName(Constant.tt_tm_id)
    private String tt_tm_id;

    @SerializedName(Constant.tt_up_u_id)
    private String tt_up_u_id;

    @SerializedName(Constant.tt_user_ranking)
    private String tt_user_ranking;

    @SerializedName(Constant.tt_user_test_ratings)
    private String tt_user_test_ratings;

    @SerializedName(Constant.tt_obtain_marks)
    private String tt_obtain_marks;

    @SerializedName(Constant.tt_comment)
    private String tt_comment;

    @SerializedName(Constant.tt_cdatetime)
    private String tt_cdatetime;

    @SerializedName(Constant.tt_status)
    private String tt_status;

    @SerializedName(Constant.tt_duration_taken)
    private String tt_duration_taken;

    @SerializedName(Constant.ttqa_id)
    private String ttqa_id;

    @SerializedName(Constant.ttqa_tq_id)
    private String ttqa_tq_id;

    @SerializedName(Constant.ttqa_tt_id)
    private String ttqa_tt_id;

    @SerializedName(Constant.ttqa_obtain_marks)
    private String ttqa_obtain_marks;

    @SerializedName(Constant.ttqa_sub_ans)
    private String ttqa_sub_ans;

    @SerializedName(Constant.ttqa_mcq_ans_1)
    private String ttqa_mcq_ans_1;

    @SerializedName(Constant.ttqa_mcq_ans_2)
    private String ttqa_mcq_ans_2;

    @SerializedName(Constant.ttqa_mcq_ans_3)
    private String ttqa_mcq_ans_3;

    @SerializedName(Constant.ttqa_mcq_ans_4)
    private String ttqa_mcq_ans_4;

    @SerializedName(Constant.ttqa_mcq_ans_5)
    private String ttqa_mcq_ans_5;

    @SerializedName(Constant.ttqa_duration_taken)
    private String ttqa_duration_taken;

    @SerializedName(Constant.ttqa_md_id)
    private String ttqa_md_id;

    @SerializedName(Constant.u_user_id)
    private String u_user_id;

    @SerializedName(Constant.sm_sub_name)
    private String sm_sub_name;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSm_sub_name() {
        return sm_sub_name;
    }

    public void setSm_sub_name(String sm_sub_name) {
        this.sm_sub_name = sm_sub_name;
    }

    //Newly Added Parameters
    @SerializedName(Constant.author)
    private String author;

    @SerializedName(Constant.quest_count)
    private String quest_count;

    @SerializedName(Constant.likeCount)
    private int likeCount;

    @SerializedName(Constant.shareCount)
    private String shareCount;

    @SerializedName(Constant.commentsCount)
    private String commentsCount;

    @SerializedName(Constant.isLike)
    private boolean isLike;

    @SerializedName(Constant.isFavourite)
    private boolean isFavourite;

    @SerializedName(Constant.publishedDate)
    private String publishedDate;

    @SerializedName(Constant.tm_attempted_by)
    private String tm_attempted_by;

    @TypeConverters(Converters.class)
    @SerializedName(Constant.DataList)
    private List<AttemptedTest> attemptedTests;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<AttemptedTest> getAttemptedTests() {
        return attemptedTests;
    }

    public void setAttemptedTests(List<AttemptedTest> attemptedTests) {
        this.attemptedTests = attemptedTests;
    }

    public String getTest_description() {
        return test_description;
    }

    public void setTest_description(String test_description) {
        this.test_description = test_description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQuest_count() {
        return quest_count;
    }

    public void setQuest_count(String quest_count) {
        this.quest_count = quest_count;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getShareCount() {
        return shareCount != null ? shareCount : "0";
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public String getCommentsCount() {
        return commentsCount != null ? commentsCount : "0";
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }


    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }


    public String getTm_attempted_by() {
        return tm_attempted_by;
    }

    public void setTm_attempted_by(String tm_attempted_by) {
        this.tm_attempted_by = tm_attempted_by;
    }

    public int getTm_id() {
        return tm_id;
    }

    public void setTm_id(int tm_id) {
        this.tm_id = tm_id;
    }

    public String getTm_sm_id() {
        return tm_sm_id;
    }

    public void setTm_sm_id(String tm_sm_id) {
        this.tm_sm_id = tm_sm_id;
    }

    public String getTm_up_u_id() {
        return tm_up_u_id;
    }

    public void setTm_up_u_id(String tm_up_u_id) {
        this.tm_up_u_id = tm_up_u_id;
    }

    public String getTm_name() {
        return tm_name;
    }

    public void setTm_name(String tm_name) {
        this.tm_name = tm_name;
    }

    public String getTm_diff_level() {
        return tm_diff_level != null ? tm_diff_level : "";
    }

    public void setTm_diff_level(String tm_diff_level) {
        this.tm_diff_level = tm_diff_level;
    }

    public String getTm_tot_marks() {
        return tm_tot_marks;
    }

    public void setTm_tot_marks(String tm_tot_marks) {
        this.tm_tot_marks = tm_tot_marks;
    }

    public String getTm_type() {
        return tm_type;
    }

    public void setTm_type(String tm_type) {
        this.tm_type = tm_type;
    }

    public String getTm_ranking() {
        return tm_ranking;
    }

    public void setTm_ranking(String tm_ranking) {
        this.tm_ranking = tm_ranking;
    }

    public String getTm_rating_count() {
        return tm_rating_count;
    }

    public void setTm_rating_count(String tm_rating_count) {
        this.tm_rating_count = tm_rating_count;
    }

    public String getTm_status() {
        return tm_status;
    }

    public void setTm_status(String tm_status) {
        this.tm_status = tm_status;
    }

    public String getTm_neg_mks() {
        return tm_neg_mks;
    }

    public void setTm_neg_mks(String tm_neg_mks) {
        this.tm_neg_mks = tm_neg_mks;
    }

    public String getTm_catg() {
        return tm_catg;
    }

    public void setTm_catg(String tm_catg) {
        this.tm_catg = tm_catg;
    }

    public String getTm_category_1() {
        return tm_category_1;
    }

    public void setTm_category_1(String tm_category_1) {
        this.tm_category_1 = tm_category_1;
    }

    public String getTm_category_2() {
        return tm_category_2;
    }

    public void setTm_category_2(String tm_category_2) {
        this.tm_category_2 = tm_category_2;
    }

    public String getTm_category_3() {
        return tm_category_3;
    }

    public void setTm_category_3(String tm_category_3) {
        this.tm_category_3 = tm_category_3;
    }

    public String getTm_avg_rating() {
        return tm_avg_rating != null ? tm_avg_rating : "";
    }

    public void setTm_avg_rating(String tm_avg_rating) {
        this.tm_avg_rating = tm_avg_rating;
    }

    public String getTm_sharable() {
        return tm_sharable;
    }

    public void setTm_sharable(String tm_sharable) {
        this.tm_sharable = tm_sharable;
    }

    public String getTm_comp_quest_count() {
        return tm_comp_quest_count;
    }

    public void setTm_comp_quest_count(String tm_comp_quest_count) {
        this.tm_comp_quest_count = tm_comp_quest_count;
    }

    public String getTm_duration() {
        return tm_duration;
    }

    public void setTm_duration(String tm_duration) {
        this.tm_duration = tm_duration;
    }

    public String getTm_cm_id() {
        return tm_cm_id;
    }

    public void setTm_cm_id(String tm_cm_id) {
        this.tm_cm_id = tm_cm_id;
    }

    public String getTm_visibility() {
        return tm_visibility;
    }

    public void setTm_visibility(String tm_visibility) {
        this.tm_visibility = tm_visibility;
    }

    public String getTq_id() {
        return tq_id;
    }

    public void setTq_id(String tq_id) {
        this.tq_id = tq_id;
    }

    public String getTq_tm_id() {
        return tq_tm_id;
    }

    public void setTq_tm_id(String tq_tm_id) {
        this.tq_tm_id = tq_tm_id;
    }

    public String getTq_q_id() {
        return tq_q_id;
    }

    public void setTq_q_id(String tq_q_id) {
        this.tq_q_id = tq_q_id;
    }

    public String getTq_marks() {
        return tq_marks;
    }

    public void setTq_marks(String tq_marks) {
        this.tq_marks = tq_marks;
    }

    public String getTq_quest_seq_num() {
        return tq_quest_seq_num;
    }

    public void setTq_quest_seq_num(String tq_quest_seq_num) {
        this.tq_quest_seq_num = tq_quest_seq_num;
    }

    public String getTq_compulsory_quest() {
        return tq_compulsory_quest;
    }

    public void setTq_compulsory_quest(String tq_compulsory_quest) {
        this.tq_compulsory_quest = tq_compulsory_quest;
    }

    public String getTq_ref_id() {
        return tq_ref_id;
    }

    public void setTq_ref_id(String tq_ref_id) {
        this.tq_ref_id = tq_ref_id;
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

    public String getTq_status() {
        return tq_status;
    }

    public void setTq_status(String tq_status) {
        this.tq_status = tq_status;
    }

    public String getTt_id() {
        return tt_id;
    }

    public void setTt_id(String tt_id) {
        this.tt_id = tt_id;
    }

    public String getTt_tm_id() {
        return tt_tm_id;
    }

    public void setTt_tm_id(String tt_tm_id) {
        this.tt_tm_id = tt_tm_id;
    }

    public String getTt_up_u_id() {
        return tt_up_u_id;
    }

    public void setTt_up_u_id(String tt_up_u_id) {
        this.tt_up_u_id = tt_up_u_id;
    }

    public String getTt_user_ranking() {
        return tt_user_ranking;
    }

    public void setTt_user_ranking(String tt_user_ranking) {
        this.tt_user_ranking = tt_user_ranking;
    }

    public String getTt_user_test_ratings() {
        return tt_user_test_ratings;
    }

    public void setTt_user_test_ratings(String tt_user_test_ratings) {
        this.tt_user_test_ratings = tt_user_test_ratings;
    }

    public String getTt_obtain_marks() {
        return tt_obtain_marks;
    }

    public void setTt_obtain_marks(String tt_obtain_marks) {
        this.tt_obtain_marks = tt_obtain_marks;
    }

    public String getTt_comment() {
        return tt_comment;
    }

    public void setTt_comment(String tt_comment) {
        this.tt_comment = tt_comment;
    }

    public String getTt_cdatetime() {
        return tt_cdatetime;
    }

    public void setTt_cdatetime(String tt_cdatetime) {
        this.tt_cdatetime = tt_cdatetime;
    }

    public String getTt_status() {
        return tt_status;
    }

    public void setTt_status(String tt_status) {
        this.tt_status = tt_status;
    }

    public String getTt_duration_taken() {
        return tt_duration_taken;
    }

    public void setTt_duration_taken(String tt_duration_taken) {
        this.tt_duration_taken = tt_duration_taken;
    }

    public String getTtqa_id() {
        return ttqa_id;
    }

    public void setTtqa_id(String ttqa_id) {
        this.ttqa_id = ttqa_id;
    }

    public String getTtqa_tq_id() {
        return ttqa_tq_id;
    }

    public void setTtqa_tq_id(String ttqa_tq_id) {
        this.ttqa_tq_id = ttqa_tq_id;
    }

    public String getTtqa_tt_id() {
        return ttqa_tt_id;
    }

    public void setTtqa_tt_id(String ttqa_tt_id) {
        this.ttqa_tt_id = ttqa_tt_id;
    }

    public String getTtqa_obtain_marks() {
        return ttqa_obtain_marks;
    }

    public void setTtqa_obtain_marks(String ttqa_obtain_marks) {
        this.ttqa_obtain_marks = ttqa_obtain_marks;
    }

    public String getTtqa_sub_ans() {
        return ttqa_sub_ans;
    }

    public void setTtqa_sub_ans(String ttqa_sub_ans) {
        this.ttqa_sub_ans = ttqa_sub_ans;
    }

    public String getTtqa_mcq_ans_1() {
        return ttqa_mcq_ans_1;
    }

    public void setTtqa_mcq_ans_1(String ttqa_mcq_ans_1) {
        this.ttqa_mcq_ans_1 = ttqa_mcq_ans_1;
    }

    public String getTtqa_mcq_ans_2() {
        return ttqa_mcq_ans_2;
    }

    public void setTtqa_mcq_ans_2(String ttqa_mcq_ans_2) {
        this.ttqa_mcq_ans_2 = ttqa_mcq_ans_2;
    }

    public String getTtqa_mcq_ans_3() {
        return ttqa_mcq_ans_3;
    }

    public void setTtqa_mcq_ans_3(String ttqa_mcq_ans_3) {
        this.ttqa_mcq_ans_3 = ttqa_mcq_ans_3;
    }

    public String getTtqa_mcq_ans_4() {
        return ttqa_mcq_ans_4;
    }

    public void setTtqa_mcq_ans_4(String ttqa_mcq_ans_4) {
        this.ttqa_mcq_ans_4 = ttqa_mcq_ans_4;
    }

    public String getTtqa_mcq_ans_5() {
        return ttqa_mcq_ans_5;
    }

    public void setTtqa_mcq_ans_5(String ttqa_mcq_ans_5) {
        this.ttqa_mcq_ans_5 = ttqa_mcq_ans_5;
    }

    public String getTtqa_duration_taken() {
        return ttqa_duration_taken;
    }

    public void setTtqa_duration_taken(String ttqa_duration_taken) {
        this.ttqa_duration_taken = ttqa_duration_taken;
    }

    public String getTtqa_md_id() {
        return ttqa_md_id;
    }

    public void setTtqa_md_id(String ttqa_md_id) {
        this.ttqa_md_id = ttqa_md_id;
    }

    public String getU_user_id() {
        return u_user_id;
    }

    public void setU_user_id(String u_user_id) {
        this.u_user_id = u_user_id;
    }

}
