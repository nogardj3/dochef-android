package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ItemWithAd<T> implements MultiItemEntity {
    public int itemType;
    public T content;

    public ItemWithAd(int itemType, T content) {
        this.itemType = itemType;
        this.content = content;
    }

    public ItemWithAd(int itemType) {
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