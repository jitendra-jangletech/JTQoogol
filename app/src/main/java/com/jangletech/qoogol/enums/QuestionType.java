package com.jangletech.qoogol.enums;

import java.util.HashMap;
import java.util.Map;

public enum QuestionType {

    SCQ("1"),
    MCQ("2"),
    Fill_THE_BLANKS("3"),
    ONE_LINE_ANSWER("4"),
    SHORT_ANSWER("5"),
    LONG_ANSWER("6");

    private String value;
    private static Map map = new HashMap<>();

    private QuestionType(String value) {
        this.value = value;
    }

    static {
        for (QuestionType questionType : QuestionType.values()) {
            map.put(questionType.value, questionType);
        }
    }

    public static QuestionType valueOf(int questType) {
        return (QuestionType) map.get(questType);
    }

    public String getValue() {
        return value;
    }
}
