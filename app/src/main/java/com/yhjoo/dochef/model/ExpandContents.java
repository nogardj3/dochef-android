package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ExpandContents implements MultiItemEntity {
    public String text;
    public long date;

    public ExpandContents(String text, long date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}