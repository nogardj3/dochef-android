package com.yhjoo.dochef.ui.adapter

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
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.SearchResult
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.utils.ImageLoadUtil

class SearchListAdapter(type: Int, data: List<SearchResult<*>?>?, layoutResId: Int) :
    BaseMultiItemQuickAdapter<SearchResult<*>?, BaseViewHolder?>(data) {

    companion object VIEWHOLDER {
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
                    ImageLoadUtil.loadUserImage(
                        mContext,
                        ele.userImg,
                        helper.getView(R.id.user_img)
                    )
                    helper.setText(R.id.user_nickname, ele.nickname)
                    helper.setText(
                        R.id.user_follower_count, String.format(
                            mContext.getString(R.string.format_follower),
                            Integer.toString(ele.follower_count)
                        )
                    )
                }
                VIEWHOLDER.ITEM_RECIPE_NAME -> {
                    val recipeItem = item.content as Recipe
                    ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem.recipeImg, helper.getView(R.id.reciperesult_recipeimg)
                    )
                    helper.setText(R.id.reciperesult_title, recipeItem.recipeName)
                    helper.setTextColor(
                        R.id.reciperesult_title,
                        mContext.getColor(R.color.colorSecondary)
                    )
                    (helper.getView<View>(R.id.reciperesult_title) as AppCompatTextView).setTypeface(
                        null,
                        Typeface.BOLD
                    )
                    helper.setText(
                        R.id.reciperesult_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem.nickname
                        )
                    )
                }
                VIEWHOLDER.ITEM_INGREDIENT -> {
                    val recipeItem2 = item.content as Recipe
                    ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem2.recipeImg, helper.getView(R.id.reciperesult_recipeimg)
                    )
                    helper.setText(R.id.reciperesult_title, recipeItem2.recipeName)
                    helper.setText(
                        R.id.reciperesult_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem2.nickname
                        )
                    )
                    helper.setVisible(R.id.reciperesult_ingredients, true)
                    (helper.getView<View>(R.id.reciperesult_ingredients) as FlexboxLayout).removeAllViews()
                    for (ingredient in recipeItem2.ingredients) {
                        val tagcontainer =
                            mLayoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
                        val tagview: AppCompatTextView =
                            tagcontainer.findViewById(R.id.vtag_post_text)
                        tagview.text = "#" + ingredient.name
                        (helper.getView<View>(R.id.reciperesult_ingredients) as FlexboxLayout).addView(
                            tagcontainer
                        )
                    }
                }
                VIEWHOLDER.ITEM_TAG -> {
                    val recipeItem3 = item.content as Recipe
                    ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem3.recipeImg, helper.getView(R.id.reciperesult_recipeimg)
                    )
                    helper.setText(R.id.reciperesult_title, recipeItem3.recipeName)
                    helper.setText(
                        R.id.reciperesult_nickname,
                        String.format(
                            mContext.resources.getString(R.string.format_usernickname),
                            recipeItem3.nickname
                        )
                    )
                    helper.setVisible(R.id.reciperesult_tags, true)
                    (helper.getView<View>(R.id.reciperesult_tags) as FlexboxLayout).removeAllViews()
                    for (tag in recipeItem3.tags) {
                        val tagcontainer =
                            mLayoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
                        val tagview: AppCompatTextView =
                            tagcontainer.findViewById(R.id.vtag_post_text)
                        tagview.text = "#$tag"
                        (helper.getView<View>(R.id.reciperesult_tags) as FlexboxLayout).addView(
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