package com.yhjoo.dochef.adapter;

import android.content.Intent;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeMultiAdapter extends BaseMultiItemQuickAdapter<MultiItemRecipe, BaseViewHolder> {
    public static final int VIEWHOLDER_AD = 1;
    public static final int VIEWHOLDER_PAGER = 2;
    public static final int VIEWHOLDER_ITEM = 3;
    public RetrofitServices.RecipeService recipeService;

    public RecipeMultiAdapter(List<MultiItemRecipe> data, RetrofitServices.RecipeService recipeService) {
        super(data);
        addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
        addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main);
        this.recipeService = recipeService;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemRecipe item) {
        switch (helper.getItemViewType()) {
            case VIEWHOLDER_ITEM:
                if (App.isServerAlive()) {
                    StorageReference sr = FirebaseStorage
                            .getInstance().getReference().child("recipe/" + item.getContent().getRecipeImg());

                    GlideApp.with(mContext)
                            .load(sr)
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.recipemain_recipeimg));
                } else
                    Glide.with(mContext)
                            .load(Integer.parseInt(item.getContent().getRecipeImg()))
                            .centerCrop()
                            .into((AppCompatImageView) helper.getView(R.id.recipemain_recipeimg));

                helper.setText(R.id.recipemain_title, item.getContent().getRecipeName());
                helper.setText(R.id.recipemain_nickname,
                        String.format(mContext.getResources().getString(R.string.format_usernickname), item.getContent().getNickname()));
                helper.setVisible(R.id.recipemain_other_group, false);

                break;

            case VIEWHOLDER_PAGER:
                helper.setText(R.id.recommend_title, item.getPager_title());
                helper.getView(R.id.recommend_more).setOnClickListener(v -> mContext.startActivity(new Intent(mContext, RecipeThemeActivity.class)));

                RecommendAdapter recommendAdapter = new RecommendAdapter();
                recommendAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Intent intent = new Intent(mContext, RecipeDetailActivity.class)
                        .putExtra("recipeID", ((Recipe)adapter.getData().get(position)).getRecipeID());
                    mContext.startActivity(intent);
                });
                RecyclerView recyclerView = helper.getView(R.id.recommend_recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(recommendAdapter);
                if(App.isServerAlive()){
                    recipeService.getRecipeByTag(item.getPager_title(),"popular")
                            .enqueue(new BasicCallback<ArrayList<Recipe>>(mContext) {
                        @Override
                        public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                            super.onResponse(call, response);

                            if (response.code() == 403)
                                App.getAppInstance().showToast("뭔가에러");
                            else {
                                recommendAdapter.setNewData(response.body());
                            }
                        }
                    });
                }
                else{
                    ArrayList<Recipe> recipes = DataGenerator.make(mContext.getResources(), mContext.getResources().getInteger(R.integer.DATE_TYPE_RECIPE));

                    recommendAdapter.setNewData(recipes);
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