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
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.classes.RecipeListItem;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeMyFragment extends Fragment {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;
    private final ArrayList<RecipeItem> recipeListItems = new ArrayList<>();
    private final String[] aa = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};
    @BindView(R.id.f_myrecipe_recycler)
    RecyclerView recyclerView;

    /*
        TODO
        1. recommendTheme 적용
        2. new review, comment 적용
        3. rating 추가
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_myrecipe, container, false);
        ButterKnife.bind(this, view);

        ArrayList<RecipeListItem> temp = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        for (int i = 0; i < temp.size(); i++) {
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, temp.get(i)));

            int tt = i%4;
            int ttt = i/4 % 2;
            if(i!=0 && tt == 0){
                if(ttt==0)
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_PAGER, aa[i % 4]));
                else
                    recipeListItems.add(new RecipeItem(VIEWHOLDER_AD));
            }
        }

        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
        recipeListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM)
                startActivity(new Intent(RecipeMyFragment.this.getActivity(), RecipeDetailActivity.class));
        });

        return view;
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

        public String getPager_title() {
            return pager_title;
        }

        private RecipeListItem getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }

    private class RecipeListAdapter extends BaseMultiItemQuickAdapter<RecipeItem, BaseViewHolder> {
        private final RequestManager requestManager;

        RecipeListAdapter(List<RecipeItem> data, RequestManager requestManager) {
            super(data);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            addItemType(VIEWHOLDER_PAGER, R.layout.v_recommend);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_f_myrecipe);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    requestManager
                            .load(item.getContent().getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_f_myrecipe_recipeimg));
                    helper.setText(R.id.li_f_myrecipe_recipetitle, item.getContent().getTitle());
                    helper.setText(R.id.li_f_myrecipe_nickname, Html.fromHtml("By - <b>" + item.getContent().getNickName() + "</b>", Html.FROM_HTML_MODE_LEGACY));

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(v -> startActivity(new Intent(RecipeMyFragment.this.getActivity(), RecipeThemeActivity.class)));

                    RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);

                    ArrayList<RecipeListItem> recipeListItems = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    RecommendAdapter recommendAdapter = new RecommendAdapter(recipeListItems, requestManager);
                    recyclerView.setAdapter(recommendAdapter);
                    recommendAdapter.setOnItemClickListener((adapter, view, position) -> startActivity(new Intent(RecipeMyFragment.this.getActivity(), RecipeDetailActivity.class)));

                    break;

                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }

        private class RecommendAdapter extends BaseQuickAdapter<RecipeListItem, BaseViewHolder> {
            private final RequestManager requestManager;

            RecommendAdapter(ArrayList<RecipeListItem> recipeListItem, RequestManager requestManager) {
                super(R.layout.li_recommend, recipeListItem);
                this.requestManager = requestManager;
            }

            @Override
            protected void convert(BaseViewHolder helper, RecipeListItem item) {
                requestManager
                        .load(item.getRecipeImg())
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
                helper.setText(R.id.li_recommend_title, item.getTitle());
                helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
            }
        }
    }
}
