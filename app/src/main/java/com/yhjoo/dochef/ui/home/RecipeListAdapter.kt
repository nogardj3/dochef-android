package com.yhjoo.dochef.ui.home

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.ValidateUtil

class RecipeListAdapter(var userID: String?) :
    BaseQuickAdapter<Recipe, BaseViewHolder>(R.layout.home_recipelist_item) {
    override fun convert(helper: BaseViewHolder, item: Recipe) {
        ImageLoaderUtil.loadRecipeImage(
            mContext,
            item.recipeImg,
            helper.getView(R.id.home_recipe_recipeimg)
        )
        helper.setText(R.id.home_recipe_name, item.recipeName)
        if (item.userID == userID) {
            helper.setVisible(R.id.home_recipe_my, true)
            helper.setVisible(R.id.home_recipe_is_favorite, false)
        } else {
            helper.setVisible(R.id.home_recipe_my, false)
            helper.setVisible(R.id.home_recipe_is_favorite, true)
        }
        helper.setVisible(R.id.home_recipe_new, ValidateUtil.checkNew(item.datetime))
    }
}