package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

public class RecipeMyListAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    String userID;

    public RecipeMyListAdapter(String userID) {
        super(R.layout.li_recipe_mylist);
        this.userID = userID;
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        ImageLoadUtil.loadRecipeImage(
                mContext,item.getRecipeImg(), helper.getView(R.id.recipemylist_recipeimg));

        helper.setText(R.id.recipemylist_recipetitle, item.getRecipeName());
        helper.setText(R.id.recipemylist_nickname,
                String.format(mContext.getResources().getString(R.string.format_usernickname), item.getNickname()));
        helper.setText(R.id.recipemylist_date, Utils.convertMillisToText(item.getDatetime()));
        helper.setText(R.id.recipemylist_rating, Integer.toString(item.getRating()));
        helper.setText(R.id.recipemylist_view, Integer.toString(item.getView_count()));

        helper.setVisible(R.id.recipemylist_yours, !item.getUserID().equals(userID));

        helper.addOnClickListener(R.id.recipemylist_yours);
    }
}