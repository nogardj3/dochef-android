package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

class MultiItemRecipe : MultiItemEntity {
    var multiType: Int
    var content: Recipe? = null
    var pagerTitle: String? = null

    constructor(itemType: Int, content: Recipe?) {
        this.multiType = itemType
        this.content = content
    }

    constructor(itemType: Int, title: String?) {
        this.multiType = itemType
        pagerTitle = title
    }

    constructor(itemType: Int) {
        this.multiType = itemType
    }

    override fun getItemType(): Int {
        return multiType
    }
}