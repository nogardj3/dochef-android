package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.RecipeListItem;

import static com.yhjoo.dochef.Preferences.temprecipes;

public class ThemeActivity extends BaseActivity {
    private final int VIEWHOLDER_AD = 1;
    private final int VIEWHOLDER_ITEM = 2;
    @BindView(R.id.theme_recycler)
    RecyclerView recyclerView;
    private RecipeListAdapter recipeListAdapter;
    private final ArrayList<ThemeItem> recipeListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_theme);
        ButterKnife.bind(this);
        MobileAds.initialize(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        for (int i = 0; i <= 3; i++) {
            Random r = new Random();

            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_ITEM, 1, new RecipeListItem("요리" + i, "만든이" + i, "메세지" + i, i, Integer.toString(temprecipes[r.nextInt(6)]))));
            recipeListItems.add(new ThemeItem(VIEWHOLDER_AD, 2));
        }

        recipeListAdapter = new RecipeListAdapter(recipeListItems, Glide.with(this));
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recipeListAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return recipeListItems.get(position).getSpanSize();
            }
        });
        recyclerView.setAdapter(recipeListAdapter);
        recipeListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                    startActivity(new Intent(ThemeActivity.this, RecipeActivity.class));
                }
            }
        });
    }

    private class ThemeItem implements MultiItemEntity {
        private final int itemType;
        private final int spanSize;

        private RecipeListItem content;

        ThemeItem(int itemType, int spanSize, RecipeListItem content) {
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

        private RecipeListItem getContent() {
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
            addItemType(VIEWHOLDER_ITEM, R.layout.li_recipe_grid);
            addItemType(VIEWHOLDER_AD, R.layout.li_tempadview);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, ThemeItem item) {
            switch (helper.getItemViewType()) {
                case VIEWHOLDER_ITEM:
                    requestManager
                            .load(Integer.valueOf(item.getContent().getRecipeImg()))
                            .apply(RequestOptions.centerCropTransform())
                            .into((AppCompatImageView) helper.getView(R.id.li_recipegrid_recipeimg));
                    helper.setText(R.id.li_recipegrid_title, item.getContent().getTitle());
                    helper.setText(R.id.li_recipegrid_nickname, "By - " + item.getContent().getNickName());
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
