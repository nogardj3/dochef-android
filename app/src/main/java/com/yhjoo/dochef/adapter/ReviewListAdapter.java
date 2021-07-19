package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.utils.Utils;

public class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
    public ReviewListAdapter() {
        super(R.layout.li_review);
    }

    @Override
    protected void convert(BaseViewHolder helper, Review item) {
        helper.setText(R.id.review_nickname, item.getNickname());
        helper.setText(R.id.review_contents, item.getContents());
        helper.setText(R.id.review_datetext, Utils.convertMillisToText(item.getDateTime()));
        helper.setRating(R.id.review_rating, item.getRating());
        helper.addOnClickListener(R.id.review_user_wrapper);
    }
}