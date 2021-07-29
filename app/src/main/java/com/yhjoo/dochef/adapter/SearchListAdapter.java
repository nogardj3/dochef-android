package com.yhjoo.dochef.adapter;

import android.graphics.Typeface;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.SearchResult;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.ImageLoadUtil;

import java.util.List;

public class SearchListAdapter extends BaseMultiItemQuickAdapter<SearchResult, BaseViewHolder> {
    public final int VIEWHOLDER_AD = 0;
    public final int VIEWHOLDER_ITEM_USER = 1;
    public final int VIEWHOLDER_ITEM_RECIPE_NAME = 2;
    public final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    public final int VIEWHOLDER_ITEM_TAG = 4;

    public SearchListAdapter(int type, List<SearchResult> data, int layoutResId) {
        super(data);
        addItemType(type, layoutResId);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResult item) {
        switch (helper.getItemViewType()) {
            case VIEWHOLDER_ITEM_USER:
                UserBrief ele = (UserBrief) item.getContent();

                ImageLoadUtil.loadUserImage(mContext, ele.getUserImg(), helper.getView(R.id.user_img));

                helper.setText(R.id.user_nickname, ele.getNickname());
                helper.setText(R.id.user_follower_count, String.format(
                        mContext.getString(R.string.format_follower), Integer.toString(ele.getFollower_count())
                ));
                break;

            case VIEWHOLDER_ITEM_RECIPE_NAME:
                Recipe recipeItem = (Recipe) item.getContent();

                ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem.getRecipeImg(), helper.getView(R.id.reciperesult_recipeimg));

                helper.setText(R.id.reciperesult_title, recipeItem.getRecipeName());
                helper.setTextColor(R.id.reciperesult_title, mContext.getColor(R.color.colorSecondary));
                ((AppCompatTextView) helper.getView(R.id.reciperesult_title)).setTypeface(null, Typeface.BOLD);
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem.getNickname()));

                break;

            case VIEWHOLDER_ITEM_INGREDIENT:
                Recipe recipeItem2 = (Recipe) item.getContent();

                ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem2.getRecipeImg(), helper.getView(R.id.reciperesult_recipeimg));

                helper.setText(R.id.reciperesult_title, recipeItem2.getRecipeName());
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem2.getNickname()));
                helper.setVisible(R.id.reciperesult_ingredients, true);

                ((FlexboxLayout) helper.getView(R.id.reciperesult_ingredients)).removeAllViews();
                for (Ingredient ingredient : recipeItem2.getIngredient()) {
                    LinearLayout tagcontainer = (LinearLayout) mLayoutInflater.inflate(R.layout.v_tag_post, null);
                    AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
                    tagview.setText("#" + ingredient.getName());
                    ((FlexboxLayout) helper.getView(R.id.reciperesult_ingredients)).addView(tagcontainer);
                }

                break;

            case VIEWHOLDER_ITEM_TAG:
                Recipe recipeItem3 = (Recipe) item.getContent();

                ImageLoadUtil.loadRecipeImage(
                        mContext, recipeItem3.getRecipeImg(), helper.getView(R.id.reciperesult_recipeimg));

                helper.setText(R.id.reciperesult_title, recipeItem3.getRecipeName());
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem3.getNickname()));
                helper.setVisible(R.id.reciperesult_tags, true);


                ((FlexboxLayout) helper.getView(R.id.reciperesult_tags)).removeAllViews();
                for (String tag : recipeItem3.getTags()) {
                    LinearLayout tagcontainer = (LinearLayout) mLayoutInflater.inflate(R.layout.v_tag_post, null);
                    AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_post_text);
                    tagview.setText("#" + tag);
                    ((FlexboxLayout) helper.getView(R.id.reciperesult_tags)).addView(tagcontainer);
                }

//                    ((TagView) helper.getView(R.id.timeline_tags)).removeAllViews();
//                    ((TagView) helper.getView(R.id.reciperesult_tags)).setTagList(recipeItem3.getTags());

                break;

            case VIEWHOLDER_AD:
                AdView mAdview = helper.getView(R.id.adview);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdview.loadAd(adRequest);
                break;
        }
    }
}