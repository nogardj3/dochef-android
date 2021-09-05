package com.yhjoo.dochef.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.MultiItemTheme
import com.yhjoo.dochef.utils.ImageLoadUtil

class RecipeMultiThemeAdapter(data: List<MultiItemTheme>?) :
    BaseMultiItemQuickAdapter<MultiItemTheme?, BaseViewHolder?>(data) {
    val VIEWHOLDER_AD = 1
    val VIEWHOLDER_ITEM = 2
    protected override fun convert(helper: BaseViewHolder, item: MultiItemTheme) {
        when (helper.itemViewType) {
            VIEWHOLDER_ITEM -> {
                ImageLoadUtil.loadRecipeImage(
                    mContext, item.content.recipeImg, helper.getView(R.id.recipetheme_img)
                )
                helper.setText(R.id.recipetheme_title, item.content.recipeName)
                helper.setText(
                    R.id.recipetheme_nickname,
                    String.format(
                        mContext.resources.getString(R.string.format_usernickname),
                        item.content.nickname
                    )
                )
                helper.setText(R.id.recipetheme_rating, String.format("%.1f", item.content.rating))
            }
            VIEWHOLDER_AD -> {
                val mAdview = helper.getView<AdView>(R.id.adview)
                val adRequest = AdRequest.Builder().build()
                mAdview.loadAd(adRequest)
            }
        }
    }

    init {
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_theme)
        addItemType(VIEWHOLDER_AD, R.layout.li_adview)
    }
}