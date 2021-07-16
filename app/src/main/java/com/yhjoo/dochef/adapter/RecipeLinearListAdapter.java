package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;

public class RecipeLinearListAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecipeLinearListAdapter() {
        super(R.layout.li_recipe_mylist);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        ImageLoadUtil.loadRecipeImage(
                mContext,item.getRecipeImg(), helper.getView(R.id.recipemylist_recipeimg));

        helper.setText(R.id.recipemylist_recipetitle, item.getRecipeName());
        helper.setText(R.id.recipemylist_nickname,
                String.format(mContext.getResources().getString(R.string.format_usernickname), item.getNickname()));
        helper.setVisible(R.id.recipemylist_mine, item.getNickname().equals("나"));
        helper.addOnClickListener(R.id.recipemylist_revise);
        helper.addOnClickListener(R.id.recipemylist_delete);
    }
}