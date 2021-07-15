package com.yhjoo.dochef.adapter;

import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.skyhope.materialtagview.TagView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.Utils;

public class PostListAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
    public PostListAdapter() {
        super(R.layout.li_timeline);
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        Utils.log(item.toString());
        if (App.isServerAlive()) {
            if (!item.getPostImg().equals("")) {
                helper.setVisible(R.id.timeline_postimg, true);
                Glide.with(mContext)
                        .load(mContext.getString(R.string.storage_image_url_post) + item.getPostImg())
                        .into((AppCompatImageView) helper.getView(R.id.timeline_postimg));
            }
            if (!item.getUserImg().equals("default"))
                Glide.with(mContext)
                        .load(mContext.getString(R.string.storage_image_url_profile) + item.getUserImg())
                        .circleCrop()
                        .into((AppCompatImageView) helper.getView(R.id.timeline_userimg));
        } else {
            helper.setVisible(R.id.timeline_postimg, true);
            Glide.with(mContext)
                    .load(Integer.parseInt(item.getPostImg()))
                    .into((AppCompatImageView) helper.getView(R.id.timeline_postimg));
            Glide.with(mContext)
                    .load(Integer.parseInt(item.getUserImg()))
                    .circleCrop()
                    .into((AppCompatImageView) helper.getView(R.id.timeline_userimg));
        }

        helper.setText(R.id.timeline_nickname, item.getNickname());
        helper.setText(R.id.timeline_likecount, Integer.toString(item.getLikes().size()));
        helper.setText(R.id.timeline_commentcount, Integer.toString(item.getLikes().size()));
        helper.setText(R.id.timeline_contents, " " + item.getContents());
        helper.setText(R.id.timeline_time, Utils.convertMillisToText(item.getDateTime()));
        helper.addOnClickListener(R.id.timeline_user_group);
        helper.addOnClickListener(R.id.timeline_comment_group);
        helper.addOnClickListener(R.id.timeline_contents);
        helper.addOnClickListener(R.id.timeline_postimg);

        ((FlexboxLayout) helper.getView(R.id.timeline_tags)).removeAllViews();
        for (String tag : item.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) mLayoutInflater.inflate(R.layout.v_tag_post,null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
            tagview.setText("#" + tag);
            ((FlexboxLayout) helper.getView(R.id.timeline_tags)).addView(tagcontainer);
        }

        if (item.getComments().size() != 0) {
            helper.setVisible(R.id.timeline_comment_group, true);
            helper.setText(R.id.timeline_comment_nickname, item.getComments().get(0).getNickName());
            helper.setText(R.id.timeline_comment_contents, item.getComments().get(0).getContents());
            helper.setText(R.id.timeline_comment_date, Utils.convertMillisToText(item.getComments().get(0).getDateTime()));

            String comment_profile_img = item.getComments().get(0).getUserImg();

            if (App.isServerAlive()) {
                if (!comment_profile_img.equals("default"))
                    Glide.with(mContext)
                            .load(mContext.getString(R.string.storage_image_url_profile) + comment_profile_img)
                            .circleCrop()
                            .into((AppCompatImageView) helper.getView(R.id.timeline_comment_img));
            }
        }
    }
}