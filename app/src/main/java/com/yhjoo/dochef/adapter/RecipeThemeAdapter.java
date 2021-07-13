package com.yhjoo.dochef.adapter;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.MultiItemTheme;

import java.util.List;

public class RecipeThemeAdapter extends BaseMultiItemQuickAdapter<MultiItemTheme, BaseViewHolder> {
    public final int VIEWHOLDER_AD = 1;
    public final int VIEWHOLDER_ITEM = 2;

    public RecipeThemeAdapter(List<MultiItemTheme> data) {
            super(data);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_theme);
            addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemTheme item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    if (App.isServerAlive()) {
                        if (!item.getContent().getRecipeImg().equals("default"))
                            Glide.with(mContext)
                                    .load(item.getContent().getRecipeImg())
                                    .apply(RequestOptions.centerCropTransform())
                                    .into((AppCompatImageView) helper.getView(R.id.recipetheme_img));
                    }
                    else{
                        Glide.with(mContext)
                                .load(Integer.parseInt(item.getContent().getRecipeImg()))
                                .apply(RequestOptions.centerCropTransform())
                                .into((AppCompatImageView) helper.getView(R.id.recipemain_recipeimg));
                    }

                    helper.setText(R.id.recipetheme_title, item.getContent().getRecipeName());
                    helper.setText(R.id.recipetheme_nickname,
                            String.format(mContext.getResources().getString(R.string.string_format_usernickname),item.getContent().getNickname()));
                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.adview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }
    }