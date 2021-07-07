package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeThemeActivity extends BaseActivity {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_ITEM = 2;
    private final ArrayList<ThemeItem> recipeListItems = new ArrayList<>();
    @BindView(R.id.recipetheme_recycler)
    RecyclerView recyclerView;

    /*
        TODO
        1. 이건 그리드이기 때문에 뷰가 다르므로 안합친다
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recipetheme);
        ButterKnife.bind(this);
        MobileAds.initialize(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipetheme_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayList<Recipe> arrayList = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        for (int i = 0; i < arrayList.size(); i++) {
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
            if (i != 0 && i % 4 == 0)
                recipeListItems.add(new ThemeItem(VIEWHOLDER_AD, 2));
        }

        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(this));
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recipeListAdapter.setSpanSizeLookup((gridLayoutManager, position) -> recipeListItems.get(position).getSpanSize());
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(RecipeThemeActivity.this, RecipeDetailActivity.class));
            }
        });
    }

    private class ThemeItem implements MultiItemEntity {
        private final int itemType;
        private final int spanSize;

        private Recipe content;

        ThemeItem(int itemType, int spanSize, Recipe content) {
            this.itemType = itemType;
            this.spanSize = spanSize;
            this.content = content;
        }

        ThemeItem(int itemType, int spanSize) {
            this.itemType = itemType;
            this.spanSize = spanSize;
        }

        private int getSpanSize() {
            return spanSize;
        }

        private Recipe getContent() {
            return content;
        }

        @Override
        public int getItemType() {
            return itemType;
        }
    }

    private class RecipeListAdapter extends BaseMultiItemQuickAdapter<ThemeItem, BaseViewHolder> {
        private final RequestManager requestManager;

        RecipeListAdapter(List<ThemeItem> data, RequestManager requestManager) {
            super(data);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipetheme);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, ThemeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    requestManager
                            .load(item.getContent().getRecipeImg())
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recipetheme_img));
                    helper.setText(R.id.li_recipetheme_title, item.getContent().getTitle());
                    helper.setText(R.id.li_recipetheme_nickname, "By - " + item.getContent().getNickName());
                    break;
                case VIEWHOLDER_AD:
                    AdView mAdview = helper.getView(R.id.tempadview);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdview.loadAd(adRequest);
                    break;
            }
        }
    }
}
