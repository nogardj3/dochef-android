package com.yhjoo.dochef.adapter

import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.model.SearchResult
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.utilities.ChefImageLoader

class SearchResultAdapter(type: Int, data: List<SearchResult<*>?>?, layoutResId: Int) :
    BaseMultiItemQuickAdapter<SearchResult<*>?, BaseViewHolder?>(data) {

    object VIEWHOLDER {
        const val AD = 0
        const val ITEM_USER = 1
        const val ITEM_RECIPE_NAME = 2
        const val ITEM_INGREDIENT = 3
        const val ITEM_TAG = 4
    }

    override fun convert(helper: BaseViewHolder?, item: SearchResult<*>?) {
        if (helper != null && item != null) {
            when (helper.itemViewType) {
                VIEWHOLDER.ITEM_USER -> {
                    val ele = item.content as UserBrief
                    ChefImageLoader.loadUserImage(
                        mContext,
                        ele.userImg,
                        helper.getView(R.id.resultuser_img)
                    )
                    helper.setText(R.id.resultuser_nickname, ele.nickname)
                    helper.setText(
                        R.id.resultuser_follower_count, String.format(
                            mContext.getString(R.string.format_follower),
                            ele.follower_count.toString()
                        )
                    )
                }
                VIEWHOLDER.ITEM_RECIPE_NAME -> {
                    val recipeItem = item.content as Recipe
                    ChefImageLoader.loadRecipeImage(
                        mContext, recipeItem.recipeImg, helper.getView(R.id.resultrecipe_recipeimg)
                    )
                    helper.setText(R.id.resultrecipe_title, recipeItem.recipeName)
                    helper.setTextColor(
                        R.id.resultrecipe_title,
                        mContext.getColor(R.color.colorSecondary)
                    )
                    (helper.getView<View>(R.id.resultrecipe_title) as AppCompatTextView).setTypeface(
                        null,
                        Typeface.BOLD
                    )
                    helper.setText(
                        R.id.resultrecipe_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem.nickname
                        )
                    )
                }
                VIEWHOLDER.ITEM_INGREDIENT -> {
                    val recipeItem2 = item.content as Recipe
                    ChefImageLoader.loadRecipeImage(
                        mContext, recipeItem2.recipeImg, helper.getView(R.id.resultrecipe_recipeimg)
                    )
                    helper.setText(R.id.resultrecipe_title, recipeItem2.recipeName)
                    helper.setText(
                        R.id.resultrecipe_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem2.nickname
                        )
                    )
                    helper.setVisible(R.id.resultrecipe_ingredients, true)
                    (helper.getView<View>(R.id.resultrecipe_ingredients) as FlexboxLayout).removeAllViews()
                    for (ingredient in recipeItem2.ingredients) {
                        val tagcontainer =
                            mLayoutInflater.inflate(R.layout.view_tag_post, null) as LinearLayout
                        val tagview: AppCompatTextView =
                            tagcontainer.findViewById(R.id.tagpost_contents)
                        tagview.text = "#$ingredient.name"
                        (helper.getView<View>(R.id.resultrecipe_ingredients) as FlexboxLayout).addView(
                            tagcontainer
                        )
                    }
                }
                VIEWHOLDER.ITEM_TAG -> {
                    val recipeItem3 = item.content as Recipe
                    ChefImageLoader.loadRecipeImage(
                        mContext, recipeItem3.recipeImg, helper.getView(R.id.resultrecipe_recipeimg)
                    )
                    helper.setText(R.id.resultrecipe_title, recipeItem3.recipeName)
                    helper.setText(
                        R.id.resultrecipe_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem3.nickname
                        )
                    )
                    helper.setVisible(R.id.resultrecipe_tags, true)
                    (helper.getView<View>(R.id.resultrecipe_tags) as FlexboxLayout).removeAllViews()
                    for (tag in recipeItem3.tags) {
                        val tagcontainer =
                            mLayoutInflater.inflate(R.layout.view_tag_post, null) as LinearLayout
                        val tagview: AppCompatTextView =
                            tagcontainer.findViewById(R.id.tagpost_contents)
                        tagview.text = "#$tag"
                        (helper.getView<View>(R.id.resultrecipe_tags) as FlexboxLayout).addView(
                            tagcontainer
                        )
                    }
                }
                VIEWHOLDER.AD -> {
                    val mAdview = helper.getView<AdView>(R.id.adview)
                    val adRequest = AdRequest.Builder().build()
                    mAdview.loadAd(adRequest)
                }
            }
        }
    }

    init {
        addItemType(type, layoutResId)
        addItemType(VIEWHOLDER.AD, R.layout.li_adview)
    }
}