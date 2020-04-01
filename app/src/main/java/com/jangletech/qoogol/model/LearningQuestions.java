package com.jangletech.qoogol.model;

/**
 * Created by Pritali on 3/18/2020.
 */
public class LearningQuestions {
    String question_id, question, questiondesc, answer, category, subject, chapter, rating, difficulty_level,
    topic, posted_on, lastused_on, likes, comments, shares,  recommended_time, attchment, marks, answerDesc,
    mcq1, mcq2, mcq3, mcq4;
    int attended_by, solve_right;
    boolean is_liked, is_fav;

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

    public String getRecommended_time() {
        return recommended_time;
    }

    public void setRecommended_time(String recommended_time) {
        this.recommended_time = recommended_time;
    }

    public String getAttchment() {
        return attchment;
    }

    public void setAttchment(String attchment) {
        this.attchment = attchment;
    }

    public boolean isIs_liked() {
        return is_liked;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    public boolean isIs_fav() {
        return is_fav;
    }

    public void setIs_fav(boolean is_fav) {
        this.is_fav = is_fav;
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

    public String getQuestion_id() {
        return question_id;
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

    public void setQuestion_id(String question_id) {
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public int getAttended_by() {
        return attended_by;
    }

    public void setAttended_by(int attended_by) {
        this.attended_by = attended_by;
    }

    public int getSolve_right() {
        return solve_right;
    }

    public void setSolve_right(int solve_right) {
        this.solve_right = solve_right;
    }
}
