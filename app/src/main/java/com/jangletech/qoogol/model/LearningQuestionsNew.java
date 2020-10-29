package com.jangletech.qoogol.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.util.Constant.FILL_THE_BLANKS;
import static com.jangletech.qoogol.util.Constant.LONG_ANSWER;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.ONE_LINE_ANSWER;
import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.SHORT_ANSWER;
import static com.jangletech.qoogol.util.Constant.TRUE_FALSE;


/**
 * Created by Pritali on 4/24/2020.
 */
@Entity
public class LearningQuestionsNew implements Cloneable, Serializable {

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    private boolean isAttempted;
    private boolean isMarked;
    private boolean isVisited;

    @PrimaryKey
    @NonNull
    @SerializedName(Constant.q_id)
    private int question_id;

    @SerializedName(Constant.q_quest)
    private String question;

    @SerializedName(Constant.q_quest_desc)
    private String questiondesc;

    @SerializedName(Constant.q_category)
    private String category;

    @SerializedName(Constant.q_trending)
    private String trending;

    @SerializedName(Constant.q_popular)
    private String popular;

    @SerializedName(Constant.q_recent)
    private String recent;

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

    @SerializedName(Constant.qlc_feedback)
    private String feedback;

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

    @SerializedName(Constant.qlc_save_flag)
    private String isSave;

    @SerializedName(Constant.CASE)
    private String fetchCASE;

    public String getFetchCASE() {
        return fetchCASE;
    }

    public void setFetchCASE(String fetchCASE) {
        this.fetchCASE = fetchCASE;
    }

    public String getIsSave() {
        return isSave != null ? isSave : "";
    }

    public void setIsSave(String isSave) {
        this.isSave = isSave;
    }

    public String getFeedback() {
        return feedback != null ? feedback : "";
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question!=null? AppUtils.decodedString(question) :"";
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestiondesc() {
        return questiondesc != null ? AppUtils.decodedString(questiondesc) : "";
    }

    public void setQuestiondesc(String questiondesc) {
        this.questiondesc = questiondesc;
    }

    public String getCategory() {
        if (getQue_option_type().equalsIgnoreCase(FILL_THE_BLANKS)) {
            return "Fill in the Blanks";
        } else if (getQue_option_type().equalsIgnoreCase(SHORT_ANSWER) || getQue_option_type().equalsIgnoreCase(SHORT_ANSWER)) {
            return "Short Answer";
        } else if (getQue_option_type().equalsIgnoreCase(LONG_ANSWER)) {
            return "Long Answer";
        } else {
            if (getQue_option_type().equalsIgnoreCase(SCQ) || getQue_option_type().equalsIgnoreCase(SCQ_IMAGE) || getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                return "SCQ";
            } else if (getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT) || getQue_option_type().equalsIgnoreCase(MCQ_IMAGE) || getQue_option_type().equalsIgnoreCase(MCQ)) {
                return "MCQ";
            } else if (getQue_option_type().equalsIgnoreCase(TRUE_FALSE)) {
                return "True False";
            } else if (getQue_option_type().equalsIgnoreCase(MATCH_PAIR)) {
                return "Match the Pairs";
            } else if (getQue_option_type().equalsIgnoreCase(MATCH_PAIR_IMAGE)) {
                return "Match the Pairs";
            } else
                return "Category";
        }
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject != null ? subject : "";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter != null ? chapter : "";
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getRating() {
        return rating != null ? UtilHelper.roundAvoid(rating) : "0";
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDifficulty_level() {
        return difficulty_level != null ? difficulty_level : "";
    }

    public void setDifficulty_level(String difficulty_level) {
        this.difficulty_level = difficulty_level;
    }

    public String getTopic() {
        return topic != null ? topic : "";
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPosted_on() {
        return posted_on != null && !posted_on.isEmpty() ? posted_on.substring(0, 10) : "";
    }

    public void setPosted_on(String posted_on) {
        this.posted_on = posted_on;
    }

    public String getLastused_on() {
        return lastused_on != null && !lastused_on.equalsIgnoreCase("") ? lastused_on.substring(0, 10) : "";
    }

    public void setLastused_on(String lastused_on) {
        this.lastused_on = lastused_on;
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

    public String getRecommended_time() {
        return recommended_time != null ? recommended_time.contains("Time") ? recommended_time : "Time: " + recommended_time + " Sec" : "";
    }

    public String getDuration() {
        return recommended_time != null ? recommended_time : "";
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
        return marks != null ? UtilHelper.formatMarks(Float.parseFloat(marks)) : "";
    }

    public String getFormatedMarks() {
        return marks != null  && !marks.isEmpty()? "Marks : " + UtilHelper.formatMarks(Float.parseFloat(marks)) : "";
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getMcq1() {
        return mcq1 != null ? AppUtils.decodedString(mcq1) : "";
    }

    public void setMcq1(String mcq1) {
        this.mcq1 = mcq1;
    }

    public String getMcq2() {
        return mcq2 != null ? AppUtils.decodedString(mcq2) : "";
    }

    public void setMcq2(String mcq2) {
        this.mcq2 = mcq2;
    }

    public String getMcq3() {
        return mcq3 != null ? AppUtils.decodedString(mcq3) : "";
    }

    public void setMcq3(String mcq3) {
        this.mcq3 = mcq3;
    }

    public String getMcq4() {
        return mcq4 != null ? AppUtils.decodedString(mcq4) : "";
    }

    public void setMcq4(String mcq4) {
        this.mcq4 = mcq4;
    }

    public String getMcq5() {
        return mcq5 != null ? mcq5 : "";
    }

    public void setMcq5(String mcq5) {
        this.mcq5 = mcq5;
    }

    public String getAttended_by() {
        return attended_by != null ? attended_by : "0";
    }

    public void setAttended_by(String attended_by) {
        this.attended_by = attended_by;
    }

    public String getSolve_right() {
        return solve_right != null ? solve_right : "";
    }

    public void setSolve_right(String solve_right) {
        this.solve_right = solve_right;
    }

    public String getIs_liked() {
        return is_liked != null ? is_liked : "";
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public String getIs_fav() {
        return is_fav != null ? is_fav : "";
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer != null ? answer : "";
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
        return que_option_type != null ? que_option_type : "";
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
        return ans_mediaId != null ? ans_mediaId : "";
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

    public String getSubject_id() {
        return subject_id != null ? subject_id : "";
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getTrending() {
        return trending != null ? trending : "";
    }

    public void setTrending(String trending) {
        this.trending = trending;
    }

    public String getPopular() {
        return popular != null ? popular : "";
    }

    public void setPopular(String popular) {
        this.popular = popular;
    }

    public String getRecent() {
        return recent != null ? recent : "";
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public int getQueTextviwVisibility() {
        return getQuestion().contains("$") ? 2 : 0;
    }

    public int getQueMathviwVisibility() {
        return getQuestion().contains("$") ? 0 : 2;
    }

    public String getImageList() {
        String imglist = "";


        List<String> img = new ArrayList<>();

        if (!getQue_option_type().equalsIgnoreCase(FILL_THE_BLANKS) || !getQue_option_type().equalsIgnoreCase(LONG_ANSWER) || !getQue_option_type().equalsIgnoreCase(SHORT_ANSWER)) {
            if (getQue_option_type().equalsIgnoreCase(SCQ_IMAGE) || getQue_option_type().equalsIgnoreCase(MCQ_IMAGE)) {
                img.add(mcq1);
                img.add(mcq2);
                img.add(mcq3);
                img.add(mcq4);
            }
            if (getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT) || getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT)) {
                img.add(getMcq1().split(":")[0]);
                img.add(getMcq2().split(":")[0]);
                img.add(getMcq3().split(":")[0]);
                img.add(getMcq4().split(":")[0]);
            }
        }

        if (img.size() > 0)
            imglist = StringUtils.join(img, ",");

        if (que_images != null)
            imglist = imglist + que_images;

        return imglist;
    }
}
