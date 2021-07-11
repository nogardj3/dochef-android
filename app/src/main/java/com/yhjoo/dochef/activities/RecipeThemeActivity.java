package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeThemeAdapter;
import com.yhjoo.dochef.databinding.ARecipethemeBinding;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.MultiItemTheme;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;

public class RecipeThemeActivity extends BaseActivity {
    public final int VIEWHOLDER_AD = 1;
    public final int VIEWHOLDER_ITEM = 2;

    ARecipethemeBinding binding;

    ArrayList<MultiItemTheme> recipeListItems = new ArrayList<>();
    
    /*
        TODO
        1. Recipe 서버 태그 검색 추가 및 기능 구현
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
            recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
            if (i != 0 && i % 4 == 0)
                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
        }
        RecipeThemeAdapter recipeThemeAdapter = new RecipeThemeAdapter(recipeListItems);
        recipeThemeAdapter.setSpanSizeLookup((gridLayoutManager, position) -> recipeListItems.get(position).getSpanSize());
        recipeThemeAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(RecipeThemeActivity.this, RecipeDetailActivity.class));
            }
        });

        binding.recipethemeRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recipethemeRecycler.setAdapter(recipeThemeAdapter);
    }
}
