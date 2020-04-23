package com.jangletech.qoogol.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TestQuestion")
public class TestQuestion {

    @PrimaryKey
    private int questId;
    private int questNo;
    private String questType;
    private String questionDesc;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;

    //Answer Saving Fields
    private boolean ans1;
    private boolean ans2;
    private boolean ans3;
    private boolean ans4;
    private boolean ans5;
    private String timeSpent;
    private boolean isMarked;
    private boolean isSaved;
    private boolean isVisited;
    private boolean isAttempted;

    private long timeLeft;

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    private double totalTime;

    public String getDescriptiveAns() {
        return descriptiveAns;
    }

    public void setDescriptiveAns(String descriptiveAns) {
        this.descriptiveAns = descriptiveAns;
    }

    //Descriptive Answer
    private String descriptiveAns;

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public boolean isAttempted() {
        return isAttempted;
    }

    public void setAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public boolean isAns1() {
        return ans1;
    }

    public void setAns1(boolean ans1) {
        this.ans1 = ans1;
    }

    public boolean isAns2() {
        return ans2;
    }

    public void setAns2(boolean ans2) {
        this.ans2 = ans2;
    }

    public boolean isAns3() {
        return ans3;
    }

    public void setAns3(boolean ans3) {
        this.ans3 = ans3;
    }

    public boolean isAns4() {
        return ans4;
    }

    public void setAns4(boolean ans4) {
        this.ans4 = ans4;
    }

    public boolean isAns5() {
        return ans5;
    }

    public void setAns5(boolean ans5) {
        this.ans5 = ans5;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public TestQuestion(int questId, int questNo, String questType, String questionDesc,
                        String ans1, String ans2, String ans3, String ans4, String ans5) {
        this.questId = questId;
        this.questNo = questNo;
        this.questType = questType;
        this.questionDesc = questionDesc;
        this.answer1 = ans1;
        this.answer2 = ans2;
        this.answer3 = ans3;
        this.answer4 = ans4;
        this.answer5 = ans5;
    }

    public TestQuestion() {

    }


    public int getQuestNo() {
        return questNo;
    }

    public void setQuestNo(int questNo) {
        this.questNo = questNo;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getQuestionDesc() {
        return questionDesc;
    }

    public void setQuestionDesc(String questionDesc) {
        this.questionDesc = questionDesc;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public void setAnswer5(String answer5) {
        this.answer5 = answer5;
    }
}
