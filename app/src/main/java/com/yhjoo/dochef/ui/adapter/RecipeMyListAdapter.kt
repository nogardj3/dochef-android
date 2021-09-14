package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class RecipeMyListAdapter(var userID: String?) :
    BaseQuickAdapter<Recipe, BaseViewHolder>(R.layout.li_recipe_mylist) {
    override fun convert(helper: BaseViewHolder, item: Recipe) {
        GlideImageLoadDelegator.loadRecipeImage(
            mContext, item.recipeImg, helper.getView(R.id.recipemylist_recipeimg)
        )
        helper.setText(R.id.recipemylist_recipetitle, item.recipeName)
        helper.setText(
            R.id.recipemylist_nickname,
            String.format(mContext.resources.getString(R.string.format_usernickname), item.nickname)
        )
        helper.setText(R.id.recipemylist_date, Utils.convertMillisToText(item.datetime))
        helper.setText(R.id.recipemylist_rating, String.format("%.1f", item.rating))
        helper.setText(R.id.recipemylist_view, item.viewCount.toString())
        helper.setVisible(R.id.recipemylist_yours, item.userID != userID)
        helper.addOnClickListener(R.id.recipemylist_yours)
    }
}