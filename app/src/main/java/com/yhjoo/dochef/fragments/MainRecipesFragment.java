package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
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
import com.yhjoo.dochef.adapter.RecommendAdapter;
import com.yhjoo.dochef.databinding.FMainRecipesBinding;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class MainRecipesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;
    public static final int mode_Recent = 1;
    public static final int mode_Popular = 2;
    private final String[] recommendTheme = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};

    FMainRecipesBinding binding;
    RecipeListAdapter recipeListAdapter;

    ArrayList<RecipeItem> recipeListItems = new ArrayList<>();

    int currentMode = 1;

    /*
        TODO
        1. Recipe 서버 추가 및 기능 구현
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainRecipesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ArrayList<Recipe> temp = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        for (int i = 0; i < temp.size(); i++) {
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, temp.get(i)));

            int tt = i % 4;
            int ttt = i / 4 % 2;
            if (i != 0 && tt == 0) {
                if (ttt == 0)
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_PAGER, recommendTheme[i % 4]));
                else
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_AD));
            }
        }

        binding.fRecipeSwipe.setOnRefreshListener(this);
        binding.fRecipeSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        recipeListAdapter = new RecipeListAdapter(recipeListItems);
        recipeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeDetailActivity.class));
            }
        });
        binding.fRecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fRecipeRecycler.setAdapter(recipeListAdapter);

        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            recipeListAdapter.setNewData(recipeListItems);
            binding.fRecipeSwipe.setRefreshing(false);
        }, 1000);
    }

    public int getAlignMode() {
        return currentMode;
    }

    public void changeAlignMode() {
        if (currentMode == mode_Recent) {
            currentMode = mode_Popular;
            recipeListAdapter.setNewData(new ArrayList<>());
            recipeListAdapter.notifyDataSetChanged();
            recipeListAdapter.setEmptyView(R.layout.rv_loading);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        App.getAppInstance().showToast("인기순");
                        recipeListAdapter.setNewData(recipeListItems);
                        recipeListAdapter.notifyDataSetChanged();
                        binding.fRecipeRecycler.getLayoutManager().scrollToPosition(0);
                    });
        } else if (currentMode == mode_Popular) {
            currentMode = mode_Recent;
            recipeListAdapter.setNewData(new ArrayList<>());
            recipeListAdapter.notifyDataSetChanged();
            recipeListAdapter.setEmptyView(R.layout.rv_loading);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        App.getAppInstance().showToast("최신순");
                        recipeListAdapter.setNewData(recipeListItems);
                        recipeListAdapter.notifyDataSetChanged();
                        binding.fRecipeRecycler.getLayoutManager().scrollToPosition(0);
                    });
        }
    }

    class RecipeItem implements MultiItemEntity {
         final int itemType;
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

        private Recipe getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }

        public String getPager_title() {
            return pager_title;
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
                    helper.setText(R.id.recipemain_viewscount, String.valueOf(item.getContent().getViewsCount()));

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(v -> startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeThemeActivity.class)));

                    RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);

                    ArrayList<Recipe> recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

                    RecommendAdapter recommendAdapter = new RecommendAdapter();
                    recommendAdapter.setOnItemClickListener((adapter, view, position)
                            -> startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeDetailActivity.class)));
                    recommendAdapter.setNewData(recipes);
                    recyclerView.setLayoutManager(
                            new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(recommendAdapter);

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.adview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }
    }
}