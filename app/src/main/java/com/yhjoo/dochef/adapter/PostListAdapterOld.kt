package com.yhjoo.dochef.adapter

import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.flexbox.FlexboxLayout
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class PostListAdapterOld : BaseQuickAdapter<Post, BaseViewHolder>(R.layout.postlist_item) {
    override fun convert(helper: BaseViewHolder, item: Post) {
        GlideImageLoadDelegator.loadPostImage(
            mContext, item.postImg, helper.getView(R.id.postlist_post_img)
        )
        GlideImageLoadDelegator.loadUserImage(
            mContext, item.userImg, helper.getView(R.id.postlist_user_img)
        )
        helper.setText(R.id.postlist_user_nickname, item.nickname)
        helper.setText(R.id.postlist_like_count, item.likes.size.toString())
        helper.setText(R.id.postlist_comment_count, item.comments.size.toString())
        helper.setText(R.id.postlist_contents, " " + item.contents)
        helper.setText(R.id.postlist_time, Utils.convertMillisToText(item.dateTime))
        helper.addOnClickListener(R.id.postlist_user_img)
        helper.addOnClickListener(R.id.postlist_user_nickname)
        (helper.getView<View>(R.id.postlist_tags) as FlexboxLayout).removeAllViews()
        for (tag in item.tags) {
            val tagcontainer = mLayoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
            val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
            tagview.text = "#$tag"
            (helper.getView<View>(R.id.postlist_tags) as FlexboxLayout).addView(tagcontainer)
        }
        if (item.comments.size != 0) {
            GlideImageLoadDelegator.loadUserImage(
                mContext, item.comments[0]!!.userImg, helper.getView(R.id.postlist_comment_user_img)
            )
            helper.setVisible(R.id.postlist_comment_group, true)
            helper.setText(R.id.postlist_comment_user_nickname, item.comments[0]!!.nickName)
            helper.setText(R.id.postlist_comment_contents, item.comments[0]!!.contents)
            helper.setText(
                R.id.postlist_comment_date, Utils.convertMillisToText(
                    item.comments[0]!!.dateTime
                )
            )
        } else helper.setVisible(R.id.postlist_comment_group, false)
    }
}