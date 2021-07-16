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

public class RecipeListAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecipeListAdapter() {
        super(R.layout.li_recipe_mylist);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        if(App.isServerAlive()) {
            StorageReference sr = FirebaseStorage
                    .getInstance().getReference().child("recipe/" + item.getRecipeImg());

            GlideApp.with(mContext)
                    .load(sr)
                    .centerCrop()
                    .into((AppCompatImageView) helper.getView(R.id.recipemylist_recipeimg));
        }
        else{
            Glide.with(mContext)
                .load(Integer.parseInt(item.getRecipeImg()))
                .centerCrop()
                .into((AppCompatImageView) helper.getView(R.id.recipemylist_recipeimg));
        }

        helper.setText(R.id.recipemylist_recipetitle, item.getRecipeName());
        helper.setText(R.id.recipemylist_nickname,
                String.format(mContext.getResources().getString(R.string.format_usernickname), item.getNickname()));
        helper.setVisible(R.id.recipemylist_mine, item.getNickname().equals("나"));
        helper.addOnClickListener(R.id.recipemylist_revise);
        helper.addOnClickListener(R.id.recipemylist_delete);
    }
}