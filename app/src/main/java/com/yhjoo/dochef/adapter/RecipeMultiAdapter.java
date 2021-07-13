package com.yhjoo.dochef.adapter;

import android.content.Intent;
import android.text.Html;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

public class RecipeMultiAdapter extends BaseMultiItemQuickAdapter<MultiItemRecipe, BaseViewHolder> {
    public static final int VIEWHOLDER_AD = 1;
    public static final int VIEWHOLDER_PAGER = 2;
    public static final int VIEWHOLDER_ITEM = 3;

    public RecipeMultiAdapter(List<MultiItemRecipe> data) {
        super(data);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemRecipe item) {
        switch (helper.getItemViewType()) {
            case VIEWHOLDER_ITEM:
                if (App.isServerAlive())
                    Glide.with(mContext)
                            .load(item.getContent().getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.recipemain_recipeimg));
                else
                    Glide.with(mContext)
                            .load(Integer.parseInt(item.getContent().getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.recipemain_recipeimg));

                helper.setText(R.id.recipemain_title, item.getContent().getTitle());
                helper.setText(R.id.recipemain_nickname,
                        String.format(mContext.getResources().getString(R.string.string_format_usernickname),item.getContent().getNickName()));
                helper.setVisible(R.id.recipemain_other_group, false);

                break;

            case VIEWHOLDER_PAGER:
                helper.setText(R.id.recommend_title, item.getPager_title());
                helper.getView(R.id.recommend_more).setOnClickListener(v -> mContext.startActivity(new Intent(mContext, RecipeThemeActivity.class)));

                RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);

                ArrayList<Recipe> recipes = DummyMaker.make(mContext.getResources(), mContext.getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

                RecommendAdapter recommendAdapter = new RecommendAdapter();
                recommendAdapter.setOnItemClickListener((adapter, view, position) -> mContext.startActivity(new Intent(mContext, RecipeDetailActivity.class)));
                recommendAdapter.setNewData(recipes);
                recyclerView.setAdapter(recommendAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

                break;

            case VIEWHOLDER_AD:
                AdView mAdview = helper.getView(R.id.adview);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdview.loadAd(adRequest);
                break;
        }
    }
}