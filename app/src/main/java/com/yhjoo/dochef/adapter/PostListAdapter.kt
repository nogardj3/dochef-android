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

class PostListAdapter : BaseQuickAdapter<Post, BaseViewHolder>(R.layout.li_timeline) {
    override fun convert(helper: BaseViewHolder, item: Post) {
        GlideImageLoadDelegator.loadPostImage(
            mContext, item.postImg, helper.getView(R.id.timeline_postimg)
        )
        GlideImageLoadDelegator.loadUserImage(
            mContext, item.userImg, helper.getView(R.id.timeline_userimg)
        )
        helper.setText(R.id.timeline_nickname, item.nickname)
        helper.setText(R.id.timeline_likecount, item.likes.size.toString())
        helper.setText(R.id.timeline_commentcount, item.comments.size.toString())
        helper.setText(R.id.timeline_contents, " " + item.contents)
        helper.setText(R.id.timeline_time, Utils.convertMillisToText(item.dateTime))
        helper.addOnClickListener(R.id.timeline_userimg)
        helper.addOnClickListener(R.id.timeline_nickname)
        (helper.getView<View>(R.id.timeline_tags) as FlexboxLayout).removeAllViews()
        for (tag in item.tags) {
            val tagcontainer = mLayoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
            val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
            tagview.text = "#$tag"
            (helper.getView<View>(R.id.timeline_tags) as FlexboxLayout).addView(tagcontainer)
        }
        if (item.comments.size != 0) {
            GlideImageLoadDelegator.loadUserImage(
                mContext, item.comments[0]!!.userImg, helper.getView(R.id.timeline_comment_img)
            )
            helper.setVisible(R.id.timeline_comment_group, true)
            helper.setText(R.id.timeline_comment_nickname, item.comments[0]!!.nickName)
            helper.setText(R.id.timeline_comment_contents, item.comments[0]!!.contents)
            helper.setText(
                R.id.timeline_comment_date, Utils.convertMillisToText(
                    item.comments[0]!!.dateTime
                )
            )
        } else helper.setVisible(R.id.timeline_comment_group, false)
    }
}