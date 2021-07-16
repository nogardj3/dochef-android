package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

public class RecipeHorizontalAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
    String userID;

    public RecipeHorizontalAdapter(String userID) {
        super(R.layout.li_recipe_home);
        this.userID = userID;
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        ImageLoadUtil.loadRecipeImage(mContext,item.getRecipeImg(), helper.getView(R.id.recipehome_recipeimg));

        helper.setText(R.id.recipehome_name, item.getRecipeName());
        if (item.getUserID().equals(userID)) {
            helper.setVisible(R.id.recipehome_my, true);
            helper.setVisible(R.id.recipehome_is_favorite, false);
        } else {
            helper.setVisible(R.id.recipehome_my, false);
            helper.setVisible(R.id.recipehome_is_favorite, true);
        }
        helper.setVisible(R.id.recipehome_new, Utils.checkNew(item.getDatetime()));
    }
}