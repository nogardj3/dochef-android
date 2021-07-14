package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultiItemRecipe implements MultiItemEntity {
    int itemType;
    Recipe content;
    String pager_title;

    public MultiItemRecipe(int itemType, Recipe content) {
        this.itemType = itemType;
        this.content = content;
    }

    public MultiItemRecipe(int itemType, String title) {
        this.itemType = itemType;
        this.pager_title = title;
    }

    public MultiItemRecipe(int itemType) {
        this.itemType = itemType;
    }

    public String getPager_title() {
        return pager_title;
    }

    public Recipe getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}