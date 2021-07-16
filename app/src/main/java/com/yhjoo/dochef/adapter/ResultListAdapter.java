package com.yhjoo.dochef.adapter;

import android.graphics.Typeface;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.MultiItemResult;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserBrief;

import java.util.List;

public class ResultListAdapter extends BaseMultiItemQuickAdapter<MultiItemResult, BaseViewHolder> {
    public final int VIEWHOLDER_AD = 0;
    public final int VIEWHOLDER_ITEM_USER = 1;
    public final int VIEWHOLDER_ITEM_RECIPE_NAME = 2;
    public final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    public final int VIEWHOLDER_ITEM_TAG = 4;

    public ResultListAdapter(int type, List<MultiItemResult> data, int layoutResId) {
        super(data);
        addItemType(type, layoutResId);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemResult item) {
        switch (helper.getItemViewType()) {
            case VIEWHOLDER_ITEM_USER:
                UserBrief ele = (UserBrief) item.getContent();

                if (App.isServerAlive()) {
                    if (!ele.getUserImg().equals("default"))
                        Glide.with(mContext)
                                .load(mContext.getString(R.string.storage_image_url_profile) + ele.getUserImg())
                                .circleCrop()
                                .into((AppCompatImageView) helper.getView(R.id.user_img));
                }

                helper.setText(R.id.user_nickname, ele.getNickname());
                break;

            case VIEWHOLDER_ITEM_RECIPE_NAME:
                Recipe recipeItem = (Recipe) item.getContent();

                if (App.isServerAlive()) {
                    StorageReference sr = FirebaseStorage
                            .getInstance().getReference().child("recipe/" + recipeItem.getRecipeImg());

                    Glide.with(mContext)
                            .load(sr)
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                } else {
                    Glide.with(mContext)
                            .load(Integer.parseInt(recipeItem.getRecipeImg()))
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                }

                helper.setText(R.id.reciperesult_title, recipeItem.getRecipeName());
                helper.setTextColor(R.id.reciperesult_title, mContext.getColor(R.color.colorPrimary));
                ((AppCompatTextView) helper.getView(R.id.reciperesult_title)).setTypeface(null, Typeface.BOLD);
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem.getNickname()));

                break;

            case VIEWHOLDER_ITEM_INGREDIENT:
                Recipe recipeItem2 = (Recipe) item.getContent();

                if (App.isServerAlive()) {
                    Glide.with(mContext)
                            .load(recipeItem2.getRecipeImg())
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                } else {
                    Glide.with(mContext)
                            .load(Integer.parseInt(recipeItem2.getRecipeImg()))
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                }

                helper.setText(R.id.reciperesult_title, recipeItem2.getRecipeName());
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem2.getNickname()));
                helper.setVisible(R.id.reciperesult_ingredients, true);

//                    ((TagView) helper.getView(R.id.timeline_tags)).removeAllViews();
//                    ((TagView) helper.getView(R.id.reciperesult_ingredients)).setTagList(((Recipe) item.getContent()).getIngredient());
                break;

            case VIEWHOLDER_ITEM_TAG:
                Recipe recipeItem3 = (Recipe) item.getContent();

                if (App.isServerAlive()) {
                    Glide.with(mContext)
                            .load(recipeItem3.getRecipeImg())
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                } else {
                    Glide.with(mContext)
                            .load(Integer.parseInt(recipeItem3.getRecipeImg()))
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.reciperesult_recipeimg));
                }

                helper.setText(R.id.reciperesult_title, recipeItem3.getRecipeName());
                helper.setText(R.id.reciperesult_nickname, String.format(mContext.getResources().getString(R.string.format_usernickname), recipeItem3.getNickname()));
                helper.setVisible(R.id.reciperesult_tags, true);

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