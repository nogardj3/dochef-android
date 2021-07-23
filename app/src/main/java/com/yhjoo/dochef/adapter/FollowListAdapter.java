package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.ImageLoadUtil;

import java.util.ArrayList;

public class FollowListAdapter extends BaseQuickAdapter<UserBrief, BaseViewHolder> {
    ArrayList<String> activeUserFollow;
    String userID;

    public FollowListAdapter(String userID) {
        super(R.layout.li_follow);
        this.userID = userID;
    }

    public void setActiveUserFollow(ArrayList<String> activeUserFollow) {
        this.activeUserFollow = activeUserFollow;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBrief item) {
        ImageLoadUtil.loadUserImage(mContext, item.getUserImg(), helper.getView(R.id.user_img));

        if (!item.getUserID().equals(userID)) {
            if (activeUserFollow.contains(item.getUserID())) {
                helper.setVisible(R.id.user_follow_btn, true);
                helper.setVisible(R.id.user_followcancel_btn, false);
            } else {
                helper.setVisible(R.id.user_follow_btn, false);
                helper.setVisible(R.id.user_followcancel_btn, true);
            }
            helper.addOnClickListener(R.id.user_followcancel_btn);
            helper.addOnClickListener(R.id.user_follow_btn);
        }

        helper.setText(R.id.user_follower_count, String.format(
                mContext.getString(R.string.format_follower), Integer.toString(item.getFollower_count())
        ));
        helper.setText(R.id.user_nickname, item.getNickname());
    }
}