package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Review;
import com.yhjoo.dochef.databinding.AReviewBinding;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;

public class ReviewActivity extends BaseActivity {
    AReviewBinding binding;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.reviewToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ReviewListAdapter reviewListAdapter = new ReviewListAdapter();
        reviewListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.reviewRecycler.getParent());
        binding.reviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.reviewRecycler.setAdapter(reviewListAdapter);

        binding.reviewFab.setImageResource(R.drawable.ic_create_white_24dp);
        binding.reviewFab.setOnClickListener(v -> startActivity(new Intent(ReviewActivity.this, ReviewWriteActivity.class)));


        ArrayList<Review> reviews = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

        reviewListAdapter.setNewData(reviews);
    }

    class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
        ReviewListAdapter() {
            super(R.layout.li_review);
        }

        @Override
        protected void convert(BaseViewHolder helper, Review item) {

            if (!item.getImageURL().equals(""))
                Glide.with(mContext)
                        .load(Integer.valueOf(item.getImageURL()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.review_recipeimg));
            else
                helper.setVisible(R.id.review_recipeimg, false);

            Glide.with(mContext)
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.review_userimg));

            helper.setRating(R.id.review_rating, item.getRating());
            helper.setText(R.id.review_nickname, item.getNickname());
            helper.setText(R.id.review_contents, item.getContents());
            helper.setText(R.id.review_date, "1일전");
        }
    }
}
