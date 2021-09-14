package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.MultiItemTheme
import com.yhjoo.dochef.utils.GlideImageLoadDelegator

class RecipeMultiThemeAdapter(data: List<MultiItemTheme>?) :
    BaseMultiItemQuickAdapter<MultiItemTheme?, BaseViewHolder?>(data) {
    object VIEWHOLDER {
        const val AD = 1
        const val ITEM = 2
    }

    init {
        addItemType(VIEWHOLDER.ITEM, R.layout.li_recipe_theme)
        addItemType(VIEWHOLDER.AD, R.layout.li_adview)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemTheme?) {
        if (helper != null && item != null) {
            when (helper.itemViewType) {
                VIEWHOLDER.ITEM -> {
                    GlideImageLoadDelegator.loadRecipeImage(
                        mContext, item.content!!.recipeImg, helper.getView(R.id.recipetheme_img)
                    )
                    helper.setText(R.id.recipetheme_title, item.content!!.recipeName)
                    helper.setText(
                        R.id.recipetheme_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            item.content!!.nickname
                        )
                    )
                    helper.setText(
                        R.id.recipetheme_rating,
                        String.format("%.1f", item.content!!.rating)
                    )
                }
                VIEWHOLDER.AD -> {
                    val mAdview = helper.getView<AdView>(R.id.adview)
                    val adRequest = AdRequest.Builder().build()
                    mAdview.loadAd(adRequest)
                }
            }
        }
    }
}