package com.jangletech.qoogol.model;

public class Notification {

    private String text;
    private int ResId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getResId() {
        return ResId;
    }

    public void setResId(int resId) {
        ResId = resId;
    }

    public Notification(String text, int resId) {
        this.text = text;
        ResId = resId;
    }

}
