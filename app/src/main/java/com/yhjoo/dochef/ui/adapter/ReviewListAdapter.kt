package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Review
import com.yhjoo.dochef.utilities.Utils

class ReviewListAdapter : BaseQuickAdapter<Review, BaseViewHolder>(R.layout.li_review) {
    override fun convert(helper: BaseViewHolder, item: Review) {
        helper.setText(R.id.review_nickname, item.nickname)
        helper.setText(R.id.review_contents, item.contents)
        helper.setText(R.id.review_datetext, Utils.convertMillisToText(item.dateTime))
        helper.setRating(R.id.review_rating, item.rating.toFloat())
        helper.addOnClickListener(R.id.review_user_wrapper)
    }
}