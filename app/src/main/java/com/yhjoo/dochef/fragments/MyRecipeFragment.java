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
import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeActivity;
import com.yhjoo.dochef.activities.ThemeActivity;
import com.yhjoo.dochef.classes.RecipeListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yhjoo.dochef.Preferences.temprecipes;

public class MyRecipeFragment extends Fragment {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_PAGER = 2;
    private final int VIEWHOLDER_ITEM = 3;
    @BindView(R.id.f_myrecipe_recycler)
    RecyclerView recyclerView;
    private final ArrayList<RecipeItem> recipeListItems = new ArrayList<>();
    private RecipeListAdapter recipeListAdapter;
    private final String[] aa = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_myrecipe, container, false);
        ButterKnife.bind(this, view);

        for (int i = 0; i <= 3; i++) {
            Random r = new Random();

            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_PAGER, aa[i % 4]));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_ITEM, new RecipeListItem("요리" + i, "이름" + i, "메세지" + i, 30, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>())));
            recipeListItems.add(new RecipeItem(VIEWHOLDER_AD));
        }

        recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
        recipeListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM)
                    startActivity(new Intent(MyRecipeFragment.this.getActivity(), RecipeActivity.class));
            }
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
                            .load(Integer.valueOf(item.getContent().getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_f_myrecipe_recipeimg));
                    helper.setText(R.id.li_f_myrecipe_recipetitle, item.getContent().getTitle());
                    helper.setText(R.id.li_f_myrecipe_nickname, Html.fromHtml("By - <b>" + item.getContent().getNickName() + "</b>"));
                    helper.setVisible(R.id.li_f_myrecipe_new, true);

                    break;

                case VIEWHOLDER_PAGER:
                    helper.setText(R.id.recommend_title, item.getPager_title());
                    helper.getView(R.id.recommend_more).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MyRecipeFragment.this.getActivity(), ThemeActivity.class));
                        }
                    });

                    RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.recommend_recyclerview);


                    ArrayList<RecipeListItem> recipeListItems = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        Random r = new Random();
                        recipeListItems.add(new RecipeListItem("추천" + i, "만든이" + i, "메세지" + i, 20, Integer.toString(temprecipes[r.nextInt(6)]), new ArrayList<>(), new ArrayList<>()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    RecommendAdapter recommendAdapter = new RecommendAdapter(recipeListItems, requestManager);
                    recyclerView.setAdapter(recommendAdapter);
                    recommendAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            startActivity(new Intent(MyRecipeFragment.this.getActivity(), RecipeActivity.class));
                        }
                    });

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
                        .load(Integer.valueOf(item.getRecipeImg()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
                helper.setText(R.id.li_recommend_title, item.getTitle());
                helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
            }
        }
    }
}
