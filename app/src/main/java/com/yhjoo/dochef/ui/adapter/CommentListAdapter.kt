package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Comment
import com.yhjoo.dochef.utils.Utils

class CommentListAdapter(private var mUserID: String) :
    BaseQuickAdapter<Comment, BaseViewHolder>(R.layout.li_comment) {
    override fun convert(helper: BaseViewHolder, item: Comment) {
        helper.setText(R.id.comment_nickname, item.nickName)
        helper.setText(R.id.comment_contents, item.contents)
        helper.setText(R.id.comment_date, Utils.convertMillisToText(item.dateTime))
        helper.setVisible(R.id.comment_other, item.userID == mUserID)
        helper.addOnClickListener(R.id.comment_other)
    }
}