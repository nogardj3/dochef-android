package com.yhjoo.dochef.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.utilities.ChefImageLoader

class RecipeHorizontalAdapter :
    BaseQuickAdapter<Recipe, BaseViewHolder>(R.layout.recipe_horizontal_item) {
    override fun convert(helper: BaseViewHolder, item: Recipe) {
        ChefImageLoader.loadRecipeImage(
            mContext, item.recipeImg, helper.getView(R.id.recipehorizontal_recipeimg)
        )
        helper.setText(R.id.recipehorizontal_title, item.recipeName)
        helper.setText(R.id.recipehorizontal_rating, String.format("%.1f", item.rating))
        helper.setText(R.id.recipehorizontal_view, item.viewCount.toString())
    }
}