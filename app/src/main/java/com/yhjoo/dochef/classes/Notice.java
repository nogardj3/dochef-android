package com.yhjoo.dochef.classes;

public class Notice {
    public final String title;
    public final String contents;
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
}