package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.GlideApp;

public class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecommendAdapter() {
        super(R.layout.li_recipe_recommend);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        if (App.isServerAlive()) {
            StorageReference sr = FirebaseStorage
                    .getInstance().getReference().child("recipe/" + item.getRecipeImg());

            GlideApp.with(mContext)
                    .load(sr)
                    .centerCrop()
                    .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));
        } else
            Glide.with(mContext)
                    .load(Integer.parseInt(item.getRecipeImg()))
                    .centerCrop()
                    .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));

        helper.setText(R.id.reciperecommend_title, item.getRecipeName());
        helper.setText(R.id.reciperecommend_nickname,
                String.format(mContext.getResources().getString(R.string.string_format_usernickname), item.getNickname()));
    }
}