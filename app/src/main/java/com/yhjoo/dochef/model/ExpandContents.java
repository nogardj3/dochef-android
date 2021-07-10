package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ExpandContents implements MultiItemEntity {
    public String text;

    public ExpandContents(String text) {
        this.text = text;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}