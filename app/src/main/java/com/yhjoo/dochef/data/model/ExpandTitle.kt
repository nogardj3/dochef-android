package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity

class ExpandTitle(var title: String?) : AbstractExpandableItem<ExpandContents?>(), MultiItemEntity {
    override fun getItemType(): Int {
        return 0
    }

    override fun getLevel(): Int {
        return 0
    }
}