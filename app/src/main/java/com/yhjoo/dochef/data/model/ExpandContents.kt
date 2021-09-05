package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

class ExpandContents(var text: String?, var date: Long) : MultiItemEntity {
    override fun getItemType(): Int {
        return 1
    }
}