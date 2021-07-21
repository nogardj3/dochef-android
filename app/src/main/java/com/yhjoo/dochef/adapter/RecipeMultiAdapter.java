package com.yhjoo.dochef.adapter;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

import java.util.List;

public class RecipeMultiAdapter extends BaseMultiItemQuickAdapter<MultiItemRecipe, BaseViewHolder> {
    public static final int VIEWHOLDER_AD = 1;
    public static final int VIEWHOLDER_ITEM = 2;

    public String userid = "";
    public boolean showNew = false;
    public boolean showYours = false;

    public RecipeMultiAdapter(List<MultiItemRecipe> data) {
        super(data);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main);
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setShowNew(boolean showNew) {
        this.showNew = showNew;
    }

    public void setShowYours(boolean showYours) {
        this.showYours = showYours;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemRecipe item) {
        switch (helper.getItemViewType()) {
            case VIEWHOLDER_ITEM:
                ImageLoadUtil.loadRecipeImage(
                        mContext, item.getContent().getRecipeImg(), helper.getView(R.id.recipemain_recipeimg));

                helper.setText(R.id.recipemain_title, item.getContent().getRecipeName());
                helper.setText(R.id.recipemain_nickname,
                        String.format(mContext.getResources().getString(R.string.format_usernickname), item.getContent().getNickname()));
                helper.setText(R.id.recipemain_date, Utils.convertMillisToText(item.getContent().getDatetime()));
                helper.setText(R.id.recipemain_rating, Integer.toString(item.getContent().getRating()));
                helper.setText(R.id.recipemain_view, Integer.toString(item.getContent().getView_count()));

                if(showNew)
                    helper.setVisible(R.id.recipemain_new,Utils.checkNew(item.getContent().getDatetime()));
                else
                    helper.setVisible(R.id.recipemain_new,false);

                if(showYours)
                    helper.setVisible(R.id.recipemain_yours, !userid.equals(item.getContent().getUserID()));
                else
                    helper.setVisible(R.id.recipemain_yours,false);

                break;

            case VIEWHOLDER_AD:
                AdView mAdview = helper.getView(R.id.adview);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdview.loadAd(adRequest);
                break;
        }
    }
}