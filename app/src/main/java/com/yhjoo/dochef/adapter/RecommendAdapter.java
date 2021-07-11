package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;

public class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecommendAdapter() {
            super(R.layout.li_recipe_recommend);
        }

        @Override
        protected void convert(BaseViewHolder helper, Recipe item) {
            if (App.isServerAlive())
                Glide.with(mContext)
                        .load(item.getRecipeImg())
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));
            else
                Glide.with(mContext)
                        .load(Integer.parseInt(item.getRecipeImg()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));

            helper.setText(R.id.reciperecommend_title, item.getTitle());
            helper.setText(R.id.reciperecommend_nickname, "By - " + item.getNickName());
        }
    }