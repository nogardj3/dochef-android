package com.yhjoo.dochef.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ExpandTitle extends AbstractExpandableItem<ExpandContents> implements MultiItemEntity {
    public String title;

    public ExpandTitle(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}