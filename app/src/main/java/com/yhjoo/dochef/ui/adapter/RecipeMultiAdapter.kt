package com.yhjoo.dochef.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.MultiItemRecipe
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class RecipeMultiAdapter(data: List<MultiItemRecipe>?) :
    BaseMultiItemQuickAdapter<MultiItemRecipe?, BaseViewHolder?>(data) {
    companion object {
        const val VIEWHOLDER_AD = 1
        const val VIEWHOLDER_ITEM = 2
    }

    init {
        addItemType(VIEWHOLDER_AD, R.layout.li_adview)
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main)
    }

    var userid: String? = null
    var showNew = false
    var showYours = false

    override fun convert(helper: BaseViewHolder?, item: MultiItemRecipe?) {
        if (helper != null && item != null) {
            when (helper.itemViewType) {
                VIEWHOLDER_ITEM -> {
                    GlideImageLoadDelegator.loadRecipeImage(
                        mContext,
                        item.content!!.recipeImg,
                        helper.getView(R.id.recipemain_recipeimg)
                    )
                    helper.setText(R.id.recipemain_title, item.content!!.recipeName)
                    helper.setText(
                        R.id.recipemain_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            item.content!!.nickname
                        )
                    )
                    helper.setText(
                        R.id.recipemain_date,
                        Utils.convertMillisToText(item.content!!.datetime)
                    )
                    helper.setText(
                        R.id.recipemain_rating,
                        String.format("%.1f", item.content!!.rating)
                    )
                    helper.setText(R.id.recipemain_view, item.content!!.viewCount.toString())
                    if (showNew) helper.setVisible(
                        R.id.recipemain_new,
                        Utils.checkNew(item.content!!.datetime)
                    ) else helper.setVisible(R.id.recipemain_new, false)
                    if (showYours) helper.setVisible(
                        R.id.recipemain_yours,
                        userid != item.content!!.userID
                    ) else helper.setVisible(R.id.recipemain_yours, false)
                }
                VIEWHOLDER_AD -> {
                    val mAdview = helper.getView<AdView>(R.id.adview)
                    val adRequest = AdRequest.Builder().build()
                    mAdview.loadAd(adRequest)
                }
            }
        }
    }
}