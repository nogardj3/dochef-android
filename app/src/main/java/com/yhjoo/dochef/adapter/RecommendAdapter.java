package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;

public class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    public RecommendAdapter() {
        super(R.layout.li_recipe_recommend);
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        ImageLoadUtil.loadRecipeImage(
                mContext,item.getRecipeImg(),helper.getView(R.id.reciperecommend_recipeimg));

        helper.setText(R.id.reciperecommend_title, item.getRecipeName());
        helper.setText(R.id.reciperecommend_nickname,
                String.format(mContext.getResources().getString(R.string.format_usernickname), item.getNickname()));
    }
}