package com.yhjoo.dochef.adapter;

import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

public class PostListAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
    public PostListAdapter() {
        super(R.layout.li_timeline);
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        ImageLoadUtil.loadPostImage(
                mContext, item.getPostImg(), helper.getView(R.id.timeline_postimg));
        ImageLoadUtil.loadUserImage(
                mContext, item.getUserImg(), helper.getView(R.id.timeline_userimg));

        helper.setText(R.id.timeline_nickname, item.getNickname());
        helper.setText(R.id.timeline_likecount, Integer.toString(item.getLikes().size()));
        helper.setText(R.id.timeline_commentcount, Integer.toString(item.getComments().size()));
        helper.setText(R.id.timeline_contents, " " + item.getContents());
        helper.setText(R.id.timeline_time, Utils.convertMillisToText(item.getDateTime()));
        helper.addOnClickListener(R.id.timeline_userimg);
        helper.addOnClickListener(R.id.timeline_nickname);

        ((FlexboxLayout) helper.getView(R.id.timeline_tags)).removeAllViews();
        for (String tag : item.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) mLayoutInflater.inflate(R.layout.v_tag_post, null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
            tagview.setText("#" + tag);
            ((FlexboxLayout) helper.getView(R.id.timeline_tags)).addView(tagcontainer);
        }

        if (item.getComments().size() != 0) {
            ImageLoadUtil.loadUserImage(
                    mContext, item.getComments().get(0).getUserImg(), helper.getView(R.id.timeline_comment_img));

            helper.setVisible(R.id.timeline_comment_group, true);
            helper.setText(R.id.timeline_comment_nickname, item.getComments().get(0).getNickName());
            helper.setText(R.id.timeline_comment_contents, item.getComments().get(0).getContents());
            helper.setText(R.id.timeline_comment_date, Utils.convertMillisToText(item.getComments().get(0).getDateTime()));
        } else
            helper.setVisible(R.id.timeline_comment_group, false);
    }
}