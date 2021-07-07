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
import com.yhjoo.dochef.classes.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class MainRecipesFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.f_recipe_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.f_recipe_recycler)
    RecyclerView recyclerView;

    public static final int mode_Recent = 1;
    public static final int mode_Popular = 2;
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;

    private final ArrayList<RecipeItem> recipeListItems = new ArrayList<>();
    private final String[] recommendTheme = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};
    private RecipeListAdapter recipeListAdapter;
    private int currentMode = 1;

    /*
        TODO
        1. 타임 컨버터 적용 -> li_recipe에 datetime 디자인 추가
        2. recommendTheme 적용
        3. rating 추가
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_main_recipes, container, false);
        ButterKnife.bind(this, view);

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

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(getContext()));
        recipeListAdapter.setOnLoadMoreListener(this, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM)
                startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeDetailActivity.class));
        });
        recipeListAdapter.setEnableLoadMore(true);

        return view;
    }

    @Override
    public void onLoadMoreRequested() {
        swipeRefreshLayout.setEnabled(false);
        recipeListAdapter.loadMoreEnd(true);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        recipeListAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(() -> {
            recipeListAdapter.setNewData(recipeListItems);
            swipeRefreshLayout.setRefreshing(false);
            recipeListAdapter.setEnableLoadMore(true);
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
                        recyclerView.getLayoutManager().scrollToPosition(0);
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
                        recyclerView.getLayoutManager().scrollToPosition(0);
                    });
        }
    }

    private class RecipeItem implements MultiItemEntity {
        private final int itemType;
        private Recipe content;
        private String pager_title;

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

    private class RecipeListAdapter extends BaseMultiItemQuickAdapter<RecipeItem, BaseViewHolder> {
        private final RequestManager requestManager;

        RecipeListAdapter(List<RecipeItem> data, RequestManager requestManager) {
            super(data);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    if (App.isServerAlive())
                        requestManager
                                .load(item.getContent().getRecipeImg())
                                .apply(RequestOptions.centerCropTransform())
                                .into((AppCompatImageView) helper.getView(R.id.li_recipe_recipeimg));
                    else
                        requestManager
                                .load(Integer.parseInt(item.getContent().getRecipeImg()))
                                .apply(RequestOptions.centerCropTransform())
                                .into((AppCompatImageView) helper.getView(R.id.li_recipe_recipeimg));

                    helper.setText(R.id.li_recipe_title, item.getContent().getTitle());
                    helper.setText(R.id.li_recipe_nickname, Html.fromHtml("By - <b>" + item.getContent().getNickName() + "</b>", Html.FROM_HTML_MODE_LEGACY));
                    helper.setText(R.id.li_recipe_viewscount, String.valueOf(item.getContent().getViewsCount()));

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(v -> startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeThemeActivity.class)));

                    recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);

                    ArrayList<Recipe> recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    RecommendAdapter recommendAdapter = new RecommendAdapter(recipes);
                    recyclerView.setAdapter(recommendAdapter);
                    recommendAdapter.setOnItemClickListener((adapter, view, position) -> startActivity(new Intent(MainRecipesFragment.this.getActivity(), RecipeDetailActivity.class)));

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }

        private class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
            RecommendAdapter(ArrayList<Recipe> recipe) {
                super(R.layout.li_recommend, recipe);
            }

            @Override
            protected void convert(BaseViewHolder helper, Recipe item) {
                if (App.isServerAlive())
                    requestManager
                            .load(item.getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
                else
                    requestManager
                            .load(Integer.parseInt(item.getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));

                helper.setText(R.id.li_recommend_title, item.getTitle());
                helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
            }
        }
    }
}