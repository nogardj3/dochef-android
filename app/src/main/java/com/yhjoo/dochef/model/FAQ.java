package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class FAQ {
    @SerializedName("title")
    public final String title;
    @SerializedName("contents")
    public final String contents;

    public FAQ(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "FAQ{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}