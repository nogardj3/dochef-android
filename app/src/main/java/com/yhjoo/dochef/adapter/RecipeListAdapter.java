package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.RecipeBrief;

import java.util.ArrayList;

public class RecipeListAdapter extends BaseQuickAdapter<RecipeBrief, BaseViewHolder> {
    public RecipeListAdapter() {
        super(R.layout.li_recipe_mylist, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, RecipeBrief item) {
        if (!item.getRecipeImg().equals("default"))
            Glide.with(mContext)
                .load(item.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into((AppCompatImageView) helper.getView(R.id.recipemylist_recipeimg));

        helper.setText(R.id.recipemylist_recipetitle, item.getRecipeName());
        helper.setText(R.id.recipemylist_nickname,
                String.format(mContext.getResources().getString(R.string.string_format_usernickname),item.getNickname()));
        helper.setVisible(R.id.recipemylist_mine, item.getNickname().equals("나"));
        helper.addOnClickListener(R.id.recipemylist_revise);
        helper.addOnClickListener(R.id.recipemylist_delete);
    }
}