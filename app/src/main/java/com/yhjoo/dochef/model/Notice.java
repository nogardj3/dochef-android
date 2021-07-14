package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class Notice {
    @SerializedName("title")
    public final String title;
    @SerializedName("contents")
    public final String contents;
    @SerializedName("datetime")
    public final long dateTime;

    public Notice(String title, String contents, long dateTime) {
        this.title = title;
        this.contents = contents;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public long getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}