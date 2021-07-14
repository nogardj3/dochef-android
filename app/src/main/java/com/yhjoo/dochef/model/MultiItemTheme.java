package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultiItemTheme implements MultiItemEntity {
    int itemType;
    int spanSize;
    Recipe content;

    public MultiItemTheme(int itemType, int spanSize, Recipe content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }

    public MultiItemTheme(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public Recipe getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}