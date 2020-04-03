package com.jangletech.qoogol.model;

public class TestQuestion {

    private int questId;
    private int questNo;
    private String questType;
    private String questionDesc;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;

    private Boolean isAnswer1Selected;
    private Boolean isAnswer2Selected;

    public Boolean getAnswer1Selected() {
        return isAnswer1Selected;
    }

    public void setAnswer1Selected(Boolean answer1Selected) {
        isAnswer1Selected = answer1Selected;
    }

    public Boolean getAnswer2Selected() {
        return isAnswer2Selected;
    }

    public void setAnswer2Selected(Boolean answer2Selected) {
        isAnswer2Selected = answer2Selected;
    }

    public Boolean getAnswer3Selected() {
        return isAnswer3Selected;
    }

    public void setAnswer3Selected(Boolean answer3Selected) {
        isAnswer3Selected = answer3Selected;
    }

    public Boolean getAnswer4Selected() {
        return isAnswer4Selected;
    }

    public void setAnswer4Selected(Boolean answer4Selected) {
        isAnswer4Selected = answer4Selected;
    }

    public Boolean getAnswer5Selected() {
        return isAnswer5Selected;
    }

    public void setAnswer5Selected(Boolean answer5Selected) {
        isAnswer5Selected = answer5Selected;
    }

    private Boolean isAnswer3Selected;
    private Boolean isAnswer4Selected;
    private Boolean isAnswer5Selected;

    public TestQuestion(int questId, int questNo, String questType, String questionDesc,
                        String answer1, String answer2, String answer3, String answer4, String answer5) {
        this.questId = questId;
        this.questNo = questNo;
        this.questType = questType;
        this.questionDesc = questionDesc;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.answer5 = answer5;
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
