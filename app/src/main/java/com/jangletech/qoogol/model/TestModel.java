package com.jangletech.qoogol.model;

public class TestModel {

    private String name;
    private String subject;
    private String duration;

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private boolean isPaused;

    public String getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(String questionCount) {
        this.questionCount = questionCount;
    }

    private String questionCount;
    private String totalMarks;
    private String difficultyLevel;
    private String ranking;
    private String attendedBy;
    private String publishedDate;
    private String ratingCount;
    private boolean isFavourite;
    private boolean isNegativeMarks;
    private String author;
    private String authorEdu;
    private String category;

    public String getRatingStarCount() {
        return ratingStarCount;
    }

    public void setRatingStarCount(String ratingStarCount) {
        this.ratingStarCount = ratingStarCount;
    }

    private String ratingStarCount;

    public TestModel(String name, String subject, String duration, String totalMarks, String difficultyLevel,
                     String ranking, String attendedBy, String publishedDate, String ratingCount,
                     boolean isFavourite, boolean isNegativeMarks, String author, String authorEdu,
                     String category,String ratingStarCount,String questionCount,boolean isPaused) {
        this.name = name;
        this.subject = subject;
        this.duration = duration;
        this.totalMarks = totalMarks;
        this.difficultyLevel = difficultyLevel;
        this.ranking = ranking;
        this.attendedBy = attendedBy;
        this.publishedDate = publishedDate;
        this.ratingCount = ratingCount;
        this.isFavourite = isFavourite;
        this.isNegativeMarks = isNegativeMarks;
        this.author = author;
        this.authorEdu = authorEdu;
        this.category = category;
        this.ratingStarCount = ratingStarCount;
        this.questionCount = questionCount;
        this.isPaused = isPaused;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getAttendedBy() {
        return attendedBy;
    }

    public void setAttendedBy(String attendedBy) {
        this.attendedBy = attendedBy;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isNegativeMarks() {
        return isNegativeMarks;
    }

    public void setNegativeMarks(boolean negativeMarks) {
        isNegativeMarks = negativeMarks;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEdu() {
        return authorEdu;
    }

    public void setAuthorEdu(String authorEdu) {
        this.authorEdu = authorEdu;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
