package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.utils.ImageLoadUtil

class RecipeHorizontalAdapter :
    BaseQuickAdapter<Recipe, BaseViewHolder>(R.layout.li_recipe_recommend) {
    override fun convert(helper: BaseViewHolder, item: Recipe) {
        ImageLoadUtil.loadRecipeImage(
            mContext, item.recipeImg, helper.getView(R.id.reciperecommend_recipeimg)
        )
        helper.setText(R.id.reciperecommend_title, item.recipeName)
        helper.setText(R.id.reciperecommend_rating, String.format("%.1f", item.rating))
        helper.setText(R.id.reciperecommend_view, item.viewCount.toString())
    }
}