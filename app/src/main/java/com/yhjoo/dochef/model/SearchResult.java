package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SearchResult<T> implements MultiItemEntity {
    public int itemType;
    public T content;

    public SearchResult(int itemType, T content) {
        this.itemType = itemType;
        this.content = content;
    }

    public SearchResult(int itemType) {
        this.itemType = itemType;
    }

    public T getContent() {
        return content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}