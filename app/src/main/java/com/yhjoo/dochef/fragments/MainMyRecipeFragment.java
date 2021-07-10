package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.databinding.FMainInitBinding;
import com.yhjoo.dochef.databinding.FMainMyrecipeBinding;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMyRecipeFragment extends Fragment {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;
    private final String[] aa = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};

    FMainMyrecipeBinding binding;

    ArrayList<RecipeItem> recipeListItems = new ArrayList<>();

    /*
        TODO
        1. recommendTheme 적용
        2. new review, comment 적용
        3. rating 추가
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainMyrecipeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ArrayList<Recipe> temp = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        for (int i = 0; i < temp.size(); i++) {
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, temp.get(i)));

            int tt = i % 4;
            int ttt = i / 4 % 2;
            if (i != 0 && tt == 0) {
                if (ttt == 0)
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_PAGER, aa[i % 4]));
                else
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_AD));
            }
        }

        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(recipeListItems);
        recipeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fMyrecipeRecycler.getParent());
        recipeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(MainMyRecipeFragment.this.getActivity(), RecipeDetailActivity.class));
            }
        });
        binding.fMyrecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fMyrecipeRecycler.setAdapter(recipeListAdapter);

        return view;
    }

    class RecipeItem implements MultiItemEntity {
        int itemType;
        Recipe content;
        String pager_title;

        RecipeItem(int itemType, Recipe content) {
            this.itemType = itemType;
            this.content = content;
        }

        RecipeItem(int itemType, String title) {
            this.itemType = itemType;
            this.pager_title = title;
        }

        RecipeItem(int itemType) {
            this.itemType = itemType;
        }

        public String getPager_title() {
            return pager_title;
        }

        private Recipe getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }

    class RecipeListAdapter extends BaseMultiItemQuickAdapter<RecipeItem, BaseViewHolder> {
        RecipeListAdapter(List<RecipeItem> data) {
            super(data);
            addItemType(VIEWHOLDER_AD, R.layout.li_adview);
            addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_main);
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeItem item) {
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
                    helper.setText(R.id.recipemain_nickname, Html.fromHtml("By - <b>" + item.getContent().getNickName() + "</b>", Html.FROM_HTML_MODE_LEGACY));
                    helper.setVisible(R.id.recipemain_other_group, false);

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(v -> startActivity(new Intent(MainMyRecipeFragment.this.getActivity(), RecipeThemeActivity.class)));

                    RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);

                    ArrayList<Recipe> recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

                    RecommendAdapter recommendAdapter = new RecommendAdapter();
                    recommendAdapter.setOnItemClickListener((adapter, view, position) -> startActivity(new Intent(MainMyRecipeFragment.this.getActivity(), RecipeDetailActivity.class)));
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

    class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
        RecommendAdapter() {
            super(R.layout.li_recipe_recommend);
        }

        @Override
        protected void convert(BaseViewHolder helper, Recipe item) {
            if (App.isServerAlive())
                Glide.with(mContext)
                        .load(item.getRecipeImg())
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));
            else
                Glide.with(mContext)
                        .load(Integer.parseInt(item.getRecipeImg()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));

            helper.setText(R.id.reciperecommend_title, item.getTitle());
            helper.setText(R.id.reciperecommend_nickname, "By - " + item.getNickName());
        }
    }
}
