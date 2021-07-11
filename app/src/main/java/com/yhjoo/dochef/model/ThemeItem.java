package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ThemeItem implements MultiItemEntity {
    final int itemType;
    final int spanSize;

    Recipe content;

    public ThemeItem(int itemType, int spanSize, Recipe content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }

    public ThemeItem(int itemType, int spanSize) {
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