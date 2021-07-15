package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.UserBrief;

public class FollowListAdapter extends BaseQuickAdapter<UserBrief, BaseViewHolder> {
    String userID;

    public FollowListAdapter(String userID) {
        super(R.layout.li_user);
        this.userID = userID;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBrief item) {
        if (App.isServerAlive()) {
            if (!item.getUserImg().equals("default")) {
                Glide.with(mContext)
                        .load(mContext.getString(R.string.storage_image_url_profile) + item.getUserImg())
                        .circleCrop()
                        .into((AppCompatImageView) helper.getView(R.id.user_img));
            }
        } else
            Glide.with(mContext)
                    .load(Integer.valueOf(item.getUserImg()))
                    .circleCrop()
                    .into((AppCompatImageView) helper.getView(R.id.user_img));

        if (!item.getUserID().equals(userID)) {
            if (item.getIs_follow() == 1)
                helper.setVisible(R.id.user_followcancel_btn, true);
            else
                helper.setVisible(R.id.user_follow_btn, true);
        }

        helper.setText(R.id.user_nickname, item.getNickname());
    }
}