package com.yhjoo.dochef.classes;

public class FAQ {
    public final String title;
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
}