package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.skyhope.materialtagview.TagView;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.ResultItem;
import com.yhjoo.dochef.model.UserBreif;
import com.yhjoo.dochef.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends BaseMultiItemQuickAdapter<ResultItem, BaseViewHolder> {
    public final int VIEWHOLDER_AD = 0;
    public final int VIEWHOLDER_ITEM_RECIPE = 1;
    public final int VIEWHOLDER_ITEM_USER = 2;
    public final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    public final int VIEWHOLDER_ITEM_TAG = 4;

        public ResultListAdapter(int type, List<ResultItem> data, int layoutResId) {
            super(data);
            addItemType(type, layoutResId);
            addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        }

        @Override
        protected void convert(BaseViewHolder helper, ResultItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM_RECIPE:
                    Glide.with(mContext)
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultrecipe_recipeimg));
                    helper.setText(R.id.li_resultrecipe_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultrecipe_nickname, "By - " + ((Recipe) item.getContent()).getNickName());
                    break;

                case VIEWHOLDER_ITEM_USER:
                    UserBreif ele = (UserBreif) item.getContent();

                    if (!ele.getUserImg().equals("default"))
                        Glide.with(mContext)
                                .load(mContext.getString(R.string.storage_image_url_profile) + ele.getUserImg())
                                .into((AppCompatImageView) helper.getView(R.id.li_resultuser_userimg));

                    helper.setText(R.id.li_resultuser_nickname, ele.getNickname());
                    break;

                case VIEWHOLDER_ITEM_INGREDIENT:
                    Glide.with(mContext)
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resultingredient_recipeimg));
                    helper.setText(R.id.li_resultingredient_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resultingredient_nickname, "By - " + ((Recipe) item.getContent()).getNickName());

                    ((TagView) helper.getView(R.id.li_resultingredient_ingredients)).removeAllViews();
                    ArrayList<String> ingredients = ((Recipe) item.getContent()).getIngredients();
                    for (int i = 0; i < ingredients.size(); i++) {
                        CustomTextView ingredienttext = new CustomTextView(mContext,mContext.getResources().getColor(R.color.colorPrimary));
                        ingredienttext.setText(ingredients.get(i));
                        ((TagView) helper.getView(R.id.li_resultingredient_ingredients)).addView(ingredienttext);
                    }
                    break;

                case VIEWHOLDER_ITEM_TAG:
                    Glide.with(mContext)
                            .load(((Recipe) item.getContent()).getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_resulttag_recipeimg));
                    helper.setText(R.id.li_resulttag_title, ((Recipe) item.getContent()).getTitle());
                    helper.setText(R.id.li_resulttag_nickname, "By - " + ((Recipe) item.getContent()).getNickName());

                    ((TagView) helper.getView(R.id.li_resulttag_tags)).setTagList(((Recipe) item.getContent()).getTags());

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }
    }