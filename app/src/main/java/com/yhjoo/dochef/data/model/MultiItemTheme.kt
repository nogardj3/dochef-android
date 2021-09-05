package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

class MultiItemTheme : MultiItemEntity {
    var multiType: Int
    var spanSize: Int
    var content: Recipe? = null

    constructor(itemType: Int, spanSize: Int, content: Recipe?) {
        this.multiType = itemType
        this.spanSize = spanSize
        this.content = content
    }

    constructor(itemType: Int, spanSize: Int) {
        this.multiType = itemType
        this.spanSize = spanSize
    }

    override fun getItemType(): Int {
        return multiType
    }
}