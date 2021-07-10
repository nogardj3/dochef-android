package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.databinding.ARecipethemeBinding;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

public class RecipeThemeActivity extends BaseActivity {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_ITEM = 2;

    ARecipethemeBinding binding;

    ArrayList<ThemeItem> recipeListItems = new ArrayList<>();
    
    /*
        TODO
        1. 서버 테마 데이터 추가
        2. retrofit 구현
        3. span size 뭐임
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipethemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.recipethemeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this);

        ArrayList<Recipe> arrayList = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));
        for (int i = 0; i < arrayList.size(); i++) {
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
            if (i != 0 && i % 4 == 0)
                recipeListItems.add(new ThemeItem(VIEWHOLDER_AD, 2));
        }
        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(recipeListItems);
        recipeListAdapter.setSpanSizeLookup((gridLayoutManager, position) -> recipeListItems.get(position).getSpanSize());
        recipeListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(RecipeThemeActivity.this, RecipeDetailActivity.class));
            }
        });

        binding.recipethemeRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recipethemeRecycler.setAdapter(recipeListAdapter);
    }

    class ThemeItem implements MultiItemEntity {
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

    class RecipeListAdapter extends BaseMultiItemQuickAdapter<ThemeItem, BaseViewHolder> {
        RecipeListAdapter(List<ThemeItem> data) {
            super(data);
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipetheme);
            addItemType(VIEWHOLDER_AD, R.layout.li_adview);
        }

        @Override
        protected void convert(BaseViewHolder helper, ThemeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    Glide.with(mContext)
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
