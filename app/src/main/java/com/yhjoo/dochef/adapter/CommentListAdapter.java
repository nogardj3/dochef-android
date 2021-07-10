package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.utils.Utils;

public class CommentListAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
    String mUserID = "";

    public CommentListAdapter(String userID) {
        super(R.layout.li_comment);
        mUserID = userID;
    }

    @Override
    protected void convert(BaseViewHolder helper, Comment item) {
        helper.setText(R.id.li_comment_nickname, item.getNickName());
        helper.setText(R.id.li_comment_contents, item.getContents());
        helper.setText(R.id.li_comment_date, Utils.convertMillisToText(item.getDateTime()));
        helper.setVisible(R.id.li_comment_other, item.getUserID().equals(mUserID));
        helper.addOnClickListener(R.id.li_comment_other);
    }
}