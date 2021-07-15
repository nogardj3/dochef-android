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
import com.yhjoo.dochef.utils.Utils;

public class RecipeHorizontalAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecipeHorizontalAdapter() {
        super(R.layout.li_recipe_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        if (App.isServerAlive()) {
            StorageReference sr = FirebaseStorage
                    .getInstance().getReference().child("recipe/" + item.getRecipeImg());

            GlideApp.with(mContext)
                    .load(sr)
                    .centerCrop()
                    .into((AppCompatImageView) helper.getView(R.id.recipehome_recipeimg));
        } else {
            Glide.with(mContext)
                    .load(Integer.valueOf(item.getRecipeImg()))
                    .centerCrop()
                    .into((AppCompatImageView) helper.getView(R.id.recipehome_recipeimg));
        }


        helper.setVisible(R.id.recipehome_new, Utils.checkNew(item.getDatetime()));
    }
}