package com.yhjoo.dochef.ui.common.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.utils.OtherUtil

class ReviewListAdapter : BaseQuickAdapter<Review, BaseViewHolder>(R.layout.review_item) {
    override fun convert(helper: BaseViewHolder, item: Review) {
        helper.setText(R.id.review_nickname, item.nickname)
        helper.setText(R.id.review_contents, item.contents)
        helper.setText(R.id.review_datetext, OtherUtil.millisToText(item.dateTime))
        helper.setRating(R.id.review_rating, item.rating.toFloat())
        helper.addOnClickListener(R.id.review_user_wrapper)
    }
}