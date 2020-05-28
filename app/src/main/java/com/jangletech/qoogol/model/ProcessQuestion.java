package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

/**
 * Created by Pritali on 4/29/2020.
 */
public class ProcessQuestion {

    @SerializedName(Constant._1223G)
    private int _1223G;

    @SerializedName(Constant.q_likes)
    private String likeCount;

    @SerializedName("Message")
    private String message;

    @SerializedName(Constant.Response)
    private String response;

    @SerializedName(Constant.commentsList)
    private List<Comments> commentList;

    @SerializedName(Constant.likesList)
    private List<Like> likeList;

    @SerializedName(Constant.q_favs)
    private String fav;

    @SerializedName(Constant.q_comments)
    private String q_comments;

    @SerializedName(Constant.q_shares)
    private String q_shares;

    @SerializedName(Constant.q_avg_ratings)
    private String ratings;

    @SerializedName(Constant.q_no_of_ratings)
    private String no_of_ratings;

    @SerializedName(Constant.attmpted_count)
    private String attmpted_count;

    @SerializedName(Constant.right_solved_count)
    private String right_solved_count;

    @SerializedName(Constant.solved_right)
    private String solved_right;

    @SerializedName(Constant.sub_ans_ratio)
    private String sub_ans_ratio;

    @SerializedName(Constant.a_sub_ans)
    private String a_sub_ans;

    public String getA_sub_ans() {
        return a_sub_ans!=null?a_sub_ans:"";
    }

    public void setA_sub_ans(String a_sub_ans) {
        this.a_sub_ans = a_sub_ans;
    }

    public String getSolved_right() {
        return solved_right!=null?solved_right:"";
    }

    public void setSolved_right(String solved_right) {
        this.solved_right = solved_right;
    }

    public String getSub_ans_ratio() {
        return sub_ans_ratio!=null?sub_ans_ratio:"";
    }

    public void setSub_ans_ratio(String sub_ans_ratio) {
        this.sub_ans_ratio = sub_ans_ratio;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }





    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Comments> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comments> commentList) {
        this.commentList = commentList;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getQ_comments() {
        return q_comments;
    }

    public void setQ_comments(String q_comments) {
        this.q_comments = q_comments;
    }

    public String getQ_shares() {
        return q_shares;
    }

    public void setQ_shares(String q_shares) {
        this.q_shares = q_shares;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getNo_of_ratings() {
        return no_of_ratings;
    }

    public void setNo_of_ratings(String no_of_ratings) {
        this.no_of_ratings = no_of_ratings;
    }

    public String getAttmpted_count() {
        return attmpted_count;
    }

    public void setAttmpted_count(String attmpted_count) {
        this.attmpted_count = attmpted_count;
    }

    public String getRight_solved_count() {
        return right_solved_count;
    }

    public void setRight_solved_count(String right_solved_count) {
        this.right_solved_count = right_solved_count;
    }
}
