package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeThemeAdapter;
import com.yhjoo.dochef.databinding.ARecipethemeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemTheme;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeThemeActivity extends BaseActivity {
    public final int VIEWHOLDER_AD = 1;
    public final int VIEWHOLDER_ITEM = 2;
    public enum MODE {NORMAL, TAG}

    ARecipethemeBinding binding;

    RetrofitServices.RecipeService recipeService;

    RecipeThemeAdapter recipeThemeAdapter;

    ArrayList<MultiItemTheme> recipeListItems = new ArrayList<>();

    String tagName;

    /*
        TODO
        1. 실행 해보고 수정할거 수정하기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipethemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.recipethemeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this);

        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);

        recipeThemeAdapter = new RecipeThemeAdapter(recipeListItems);
        recipeThemeAdapter.setSpanSizeLookup((gridLayoutManager, position) -> recipeListItems.get(position).getSpanSize());
        recipeThemeAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(RecipeThemeActivity.this, RecipeDetailActivity.class));
            }
        });

        binding.recipethemeRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recipethemeRecycler.setAdapter(recipeThemeAdapter);

        if (App.isServerAlive()) {
            getRecipeList();
        } else {
            ArrayList<Recipe> arrayList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE));
            for (int i = 0; i < arrayList.size(); i++) {
                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
            }

            recipeThemeAdapter.setNewData(recipeListItems);
        }
    }

    void getRecipeList() {
        recipeService.getRecipeByTag("매운맛","popular")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<Recipe> arrayList = response.body();
                            for (int i = 0; i < arrayList.size(); i++) {
                                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
                                if (i != 0 && i % 4 == 0)
                                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                            }

                            recipeThemeAdapter.setNewData(recipeListItems);
                            recipeThemeAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipethemeRecycler.getParent());
                        }
                    }
                });
    }
}
