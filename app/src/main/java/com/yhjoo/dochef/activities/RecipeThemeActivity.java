package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeMultiThemeAdapter;
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

    public enum MODE {POPULAR, TAG}

    ARecipethemeBinding binding;
    RetrofitServices.RecipeService recipeService;
    RecipeMultiThemeAdapter recipeMultiThemeAdapter;

    ArrayList<MultiItemTheme> recipeListItems = new ArrayList<>();
    String tagName;

    MODE currentMode;

    /*
        TODO
        recommend에서 넘어옴
        recommend는 10개까지만, 여기는 다보여줌
        recommend 디자인에 + 하기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipethemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.recipethemeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this);

        if(getIntent().getStringExtra("tag") == null)
            currentMode = MODE.POPULAR;
        else{
            currentMode = MODE.TAG;
            tagName = getIntent().getStringExtra("tag");
        }

        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);

        recipeMultiThemeAdapter = new RecipeMultiThemeAdapter(recipeListItems);
        recipeMultiThemeAdapter.setSpanSizeLookup((gridLayoutManager, position) -> recipeListItems.get(position).getSpanSize());
        recipeMultiThemeAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                Intent intent = new Intent(RecipeThemeActivity.this, RecipeDetailActivity.class)
                        .putExtra("recipeID", recipeListItems.get(position).getContent().getRecipeID());
                startActivity(intent);
            }
        });

        binding.recipethemeRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recipethemeRecycler.setAdapter(recipeMultiThemeAdapter);

        if (App.isServerAlive()){
            if(currentMode == MODE.POPULAR)
                getRecipeList();
            else
                getRecipeListbyTag();
        }
        else {
            ArrayList<Recipe> arrayList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
            for (int i = 0; i < arrayList.size(); i++) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
            }

            recipeMultiThemeAdapter.setNewData(recipeListItems);
        }
    }

    void getRecipeList() {
        recipeService.getRecipes("popular")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<Recipe> arrayList = response.body();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i != 0 && i % 4 == 0)
                                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
                            }

                            recipeMultiThemeAdapter.setNewData(recipeListItems);
                            recipeMultiThemeAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipethemeRecycler.getParent());
                        }
                    }
                });
    }

    void getRecipeListbyTag() {
        recipeService.getRecipeByTag(tagName, "popular")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<Recipe> arrayList = response.body();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i != 0 && i % 4 == 0)
                                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
                            }

                            recipeMultiThemeAdapter.setNewData(recipeListItems);
                            recipeMultiThemeAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipethemeRecycler.getParent());
                        }
                    }
                });
    }
}