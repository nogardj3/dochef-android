package com.yhjoo.dochef.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

class SearchResult<T> : MultiItemEntity {
    var itemType: Int
    var content: T? = null

    constructor(itemType: Int, content: T) {
        this.itemType = itemType
        this.content = content
    }

    constructor(itemType: Int) {
        this.itemType = itemType
    }

    override fun getItemType(): Int {
        return itemType
    }
}