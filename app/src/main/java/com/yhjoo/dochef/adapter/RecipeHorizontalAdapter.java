package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;

public class RecipeHorizontalAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecipeHorizontalAdapter() {
        super(R.layout.li_recipe_recommend);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        ImageLoadUtil.loadRecipeImage(
                mContext, item.getRecipeImg(), helper.getView(R.id.reciperecommend_recipeimg));

        helper.setText(R.id.reciperecommend_title, item.getRecipeName());
        helper.setText(R.id.reciperecommend_rating, Integer.toString(item.getRating()));
        helper.setText(R.id.reciperecommend_view, Integer.toString(item.getView_count()));
    }
}