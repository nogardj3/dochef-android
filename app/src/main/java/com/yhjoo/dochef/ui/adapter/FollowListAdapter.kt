package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import java.util.*

class FollowListAdapter(var userID: String) :
    BaseQuickAdapter<UserBrief, BaseViewHolder>(R.layout.li_follow) {

    private lateinit var userFollow: ArrayList<String>
    fun settingUserFollow(activeUserFollow: ArrayList<String>) {
        this.userFollow = activeUserFollow
    }

    override fun convert(helper: BaseViewHolder, item: UserBrief) {
        GlideImageLoadDelegator.loadUserImage(mContext, item.userImg, helper.getView(R.id.user_img))
        if (item.userID != userID) {
            if (userFollow.contains(item.userID)) {
                helper.setVisible(R.id.user_follow_btn, true)
                helper.setVisible(R.id.user_followcancel_btn, false)
            } else {
                helper.setVisible(R.id.user_follow_btn, false)
                helper.setVisible(R.id.user_followcancel_btn, true)
            }
            helper.addOnClickListener(R.id.user_followcancel_btn)
            helper.addOnClickListener(R.id.user_follow_btn)
        }
        helper.setText(
            R.id.user_follower_count, String.format(
                mContext.getString(R.string.format_follower), item.follower_count.toString()
            )
        )
        helper.setText(R.id.user_nickname, item.nickname)
    }
}