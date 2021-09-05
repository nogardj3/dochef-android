package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

class SearchResult<T> : MultiItemEntity {
    var multiType: Int
    var content: T? = null

    constructor(itemType: Int, content: T) {
        this.multiType = itemType
        this.content = content
    }

    constructor(itemType: Int) {
        this.multiType = itemType
    }

    override fun getItemType(): Int {
        return multiType
    }
}