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
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.MultiItemTheme;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class RecipeThemeActivity extends BaseActivity {
    public final int VIEWHOLDER_AD = 1;
    public final int VIEWHOLDER_ITEM = 2;

    public enum MODE {POPULAR, TAG}

    ARecipethemeBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RecipeMultiThemeAdapter recipeMultiThemeAdapter;

    ArrayList<MultiItemTheme> recipeListItems = new ArrayList<>();
    String tagName;

    MODE currentMode;

    /*
        TODO
        Recommend multi adapter 변경
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipethemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.recipethemeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this);

        if (getIntent().getStringExtra("tag") == null)
            currentMode = MODE.POPULAR;
        else {
            currentMode = MODE.TAG;
            tagName = getIntent().getStringExtra("tag");
        }

        recipeService = RxRetrofitBuilder.create(this, RxRetrofitServices.RecipeService.class);

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            loadData();
        } else {
            ArrayList<Recipe> arrayList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
            for (int i = 0; i < arrayList.size(); i++) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
            }

            recipeMultiThemeAdapter.setNewData(recipeListItems);
        }
    }

    void loadData() {
        Single<Response<ArrayList<Recipe>>> recipeSingle;

        if (currentMode == MODE.POPULAR)
            recipeSingle = recipeService.getRecipes("popular")
                    .observeOn(AndroidSchedulers.mainThread());
        else
            recipeSingle = recipeService.getRecipeByTag(tagName, "popular")
                    .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(
                recipeSingle
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            ArrayList<Recipe> arrayList = response.body();

                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i != 0 && i % 4 == 0)
                                    recipeListItems.add(new MultiItemTheme(VIEWHOLDER_AD, 2));
                                recipeListItems.add(new MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList.get(i)));
                            }

                            recipeMultiThemeAdapter.setNewData(recipeListItems);
                            recipeMultiThemeAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipethemeRecycler.getParent());
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}