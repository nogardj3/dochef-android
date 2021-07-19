package com.yhjoo.dochef.adapter;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeMultiAdapter extends BaseMultiItemQuickAdapter<MultiItemRecipe, BaseViewHolder> {
    public static final int VIEWHOLDER_AD = 1;
    public static final int VIEWHOLDER_PAGER = 2;
    public static final int VIEWHOLDER_ITEM = 3;
    public RetrofitServices.RecipeService recipeService;

    public String userid = "";
    public boolean showNew = false;
    public boolean showYours = false;

    // TODO
    // 이거 좀 아닌듯

    public RecipeMultiAdapter(List<MultiItemRecipe> data, RetrofitServices.RecipeService recipeService) {
        super(data);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main);
        this.recipeService = recipeService;
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

            case VIEWHOLDER_PAGER:
                helper.setText(R.id.recommend_title, item.getPager_title());
                helper.getView(R.id.recommend_more).setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, RecipeThemeActivity.class)
                            .putExtra("tag", item.getPager_title());
                    mContext.startActivity(intent);
                });

                RecipeHorizontalAdapter recipeHorizontalAdapter = new RecipeHorizontalAdapter();
                recipeHorizontalAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Intent intent = new Intent(mContext, RecipeDetailActivity.class)
                            .putExtra("recipeID", ((Recipe) adapter.getData().get(position)).getRecipeID());
                    mContext.startActivity(intent);
                });
                RecyclerView recyclerView = helper.getView(R.id.recommend_recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(recipeHorizontalAdapter);
                if (App.isServerAlive()) {
                    recipeService.getRecipeByTag(item.getPager_title(), "popular")
                            .enqueue(new BasicCallback<ArrayList<Recipe>>(mContext) {
                                @Override
                                public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                                    super.onResponse(call, response);

                                    if (response.code() == 403)
                                        App.getAppInstance().showToast("뭔가에러");
                                    else {
                                        recipeHorizontalAdapter.setNewData(response.body());
                                    }
                                }
                            });
                } else {
                    ArrayList<Recipe> recipes = DataGenerator.make(mContext.getResources(), mContext.getResources().getInteger(R.integer.DATE_TYPE_RECIPE));

                    recipeHorizontalAdapter.setNewData(recipes);
                }
                break;

            case VIEWHOLDER_AD:
                AdView mAdview = helper.getView(R.id.adview);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdview.loadAd(adRequest);
                break;
        }
    }
}