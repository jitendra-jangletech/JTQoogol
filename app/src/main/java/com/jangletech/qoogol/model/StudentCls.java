package com.jangletech.qoogol.model;

import java.io.Serializable;

public class StudentCls implements Serializable {

    private int stuClass;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public StudentCls(int stuClass) {
        this.stuClass = stuClass;
    }

    public int getStuClass() {
        return stuClass;
    }

    public void setStuClass(int stuClass) {
        this.stuClass = stuClass;
    }
}
