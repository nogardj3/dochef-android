package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.clans.fab.FloatingActionButton;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Review;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends BaseActivity {
    @BindView(R.id.review_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.review_fab)
    FloatingActionButton floatingActionButton;

    /*
        TODO
        1. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_review);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewListAdapter);

        reviewListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        reviewListAdapter.setOnItemChildClickListener((baseQuickAdapter, view, i) -> {
            if (baseQuickAdapter.getViewByPosition(recyclerView, i, R.id.review_comment_container).getVisibility() == View.VISIBLE)
                baseQuickAdapter.getViewByPosition(recyclerView, i, R.id.review_comment_container).setVisibility(View.GONE);
            else
                baseQuickAdapter.getViewByPosition(recyclerView, i, R.id.review_comment_container).setVisibility(View.VISIBLE);
        });

        floatingActionButton.setImageResource(R.drawable.ic_create_white_24dp);
        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(ReviewActivity.this, ReviewWriteActivity.class)));


        ArrayList<Review> reviews = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

        reviewListAdapter.setNewData(reviews);
    }

    private class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
        private final RequestManager requestManager;

        ReviewListAdapter(RequestManager requestManager) {
            super(R.layout.li_review);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Review item) {
            if (!item.getImageURL().equals(""))
                requestManager
                        .load(Integer.valueOf(item.getImageURL()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.review_recipeimg));
            else
                helper.setVisible(R.id.review_recipeimg, false);

            requestManager
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.review_userimg));

            helper.setRating(R.id.review_rating, item.getRating());
            helper.setText(R.id.review_nickname, item.getNickname());
            helper.setText(R.id.review_contents, item.getContents());
            helper.setText(R.id.review_date, "1일전");
            helper.addOnClickListener(R.id.review_comment_write);
        }
    }
}
