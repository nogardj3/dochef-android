package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.Utils

class RecipeHorizontalHomeAdapter(var userID: String?) :
    BaseQuickAdapter<Recipe, BaseViewHolder>(R.layout.li_recipe_home) {
    override fun convert(helper: BaseViewHolder, item: Recipe) {
        ImageLoadUtil.loadRecipeImage(
            mContext,
            item.recipeImg,
            helper.getView(R.id.recipehome_recipeimg)
        )
        helper.setText(R.id.recipehome_name, item.recipeName)
        if (item.userID == userID) {
            helper.setVisible(R.id.recipehome_my, true)
            helper.setVisible(R.id.recipehome_is_favorite, false)
        } else {
            helper.setVisible(R.id.recipehome_my, false)
            helper.setVisible(R.id.recipehome_is_favorite, true)
        }
        helper.setVisible(R.id.recipehome_new, Utils.checkNew(item.datetime))
    }
}