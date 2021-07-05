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
import com.yhjoo.dochef.activities.RecipeActivity;
import com.yhjoo.dochef.activities.ThemeActivity;
import com.yhjoo.dochef.classes.RecipeListItem;
import com.yhjoo.dochef.views.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

import static com.yhjoo.dochef.Preferences.temprecipes;

public class RecipeFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int mode_Recent = 1;
    public static final int mode_Popular = 2;
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;
    private final ArrayList<RecipeItem> recipeListItems = new ArrayList<>();
    private final String[] aa = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};
    @BindView(R.id.f_recipe_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.f_recipe_recycler)
    RecyclerView recyclerView;
    private RecipeListAdapter recipeListAdapter;
    private int currentMode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_recipe, container, false);
        ButterKnife.bind(this, view);

        for (int i = 0; i <= 3; i++) {
            Random r = new Random();

            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_PAGER, aa[i % 4]));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_AD));
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(getContext()));
        recipeListAdapter.setOnLoadMoreListener(this, recyclerView);
        recipeListAdapter.setLoadMoreView(new CustomLoadMoreView());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM)
                startActivity(new Intent(RecipeFragment.this.getActivity(), RecipeActivity.class));
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
        private RecipeListItem content;
        private String pager_title;

        RecipeItem(int itemType, RecipeListItem content) {
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

        private RecipeListItem getContent() {
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
                    requestManager
                            .load(Integer.valueOf(item.getContent().getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recipe_recipeimg));
                    helper.setText(R.id.li_recipe_title, item.getContent().getTitle());
                    helper.setText(R.id.li_recipe_nickname, Html.fromHtml("By - <b>" + item.getContent().getNickName() + "</b>"));
                    helper.setText(R.id.li_recipe_viewscount, String.valueOf(item.getContent().getViewsCount()));

                    helper.setVisible(R.id.li_recipe_viewscountlayout, currentMode == mode_Popular);
                    helper.setVisible(R.id.li_recipe_date_layout, currentMode == mode_Recent);

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(v -> startActivity(new Intent(RecipeFragment.this.getActivity(), ThemeActivity.class)));

                    recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);
                    ArrayList<RecipeListItem> recipeListItems = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        Random r = new Random();
                        recipeListItems.add(new RecipeListItem("추천" + i, "만든이" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    RecommendAdapter recommendAdapter = new RecommendAdapter(recipeListItems);
                    recyclerView.setAdapter(recommendAdapter);
                    recommendAdapter.setOnItemClickListener((adapter, view, position) -> startActivity(new Intent(RecipeFragment.this.getActivity(), RecipeActivity.class)));

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }

        private class RecommendAdapter extends BaseQuickAdapter<RecipeListItem, BaseViewHolder> {
            RecommendAdapter(ArrayList<RecipeListItem> recipeListItem) {
                super(R.layout.li_recommend, recipeListItem);
            }

            @Override
            protected void convert(BaseViewHolder helper, RecipeListItem item) {
                requestManager
                        .load(Integer.valueOf(item.getRecipeImg()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
                helper.setText(R.id.li_recommend_title, item.getTitle());
                helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
            }
        }
    }
}