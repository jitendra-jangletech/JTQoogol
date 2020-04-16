package com.jangletech.qoogol.model;

public class QuestAnswer {

    private int qId;
    private String qType;
    private boolean ans1;
    private boolean ans2;
    private boolean ans3;
    private boolean ans4;
    private boolean ans5;
    private String timeSpent;
    private boolean isMarked;
    private boolean isSaved;

    public int getqId() {
        return qId;
    }

    public void setqId(int qId) {
        this.qId = qId;
    }

    public String getqType() {
        return qType;
    }

    public void setqType(String qType) {
        this.qType = qType;
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
}
