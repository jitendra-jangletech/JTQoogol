package com.jangletech.qoogol.model;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

public class SyllabusChapter {

    @SerializedName(Constant.cm_id)
    private String chapterId;
    @SerializedName(Constant.cm_chapter_name)
    private String chapterName;

    public String getChapterId() {
        return chapterId != null ? chapterId : "";
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName != null ? chapterName : "";
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}