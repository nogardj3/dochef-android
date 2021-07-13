package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultiItemRecipe implements MultiItemEntity {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;

    int itemType;
    RecipeBrief content;
    String pager_title;

    public MultiItemRecipe(int itemType, RecipeBrief content) {
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

    public RecipeBrief getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}