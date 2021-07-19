package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeMyListAdapter;
import com.yhjoo.dochef.databinding.ARecipelistBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class RecipeMyListActivity extends BaseActivity {
    ARecipelistBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RecipeMyListAdapter recipeMyListAdapter;

    MenuItem addMenu;

    ArrayList<Recipe> recipeList = new ArrayList<>();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipelistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.recipelistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipeService = RxRetrofitBuilder.create(this, RxRetrofitServices.RecipeService.class);

        userID = Utils.getUserBrief(this).getUserID();

        recipeMyListAdapter = new RecipeMyListAdapter(userID);
        recipeMyListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.recipelistRecycler.getParent());
        recipeMyListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(RecipeMyListActivity.this, RecipeDetailActivity.class)
                        .putExtra("recipeID", recipeList.get(position).getRecipeID());
                startActivity(intent);
            }
        });
        recipeMyListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.recipemylist_yours) {
                createConfirmDialog(this,
                        null, "레시피를 삭제하시겠습니까?",
                        (dialog1, which) ->
                                cancelLikeRecipe(((Recipe) adapter.getData().get(position)).getRecipeID()))
                        .show();
            }
        });
        binding.recipelistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recipelistRecycler.setAdapter(recipeMyListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.isServerAlive())
            loadData();
        else {
            recipeList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));

            recipeMyListAdapter.setNewData(recipeList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_add, menu);

        addMenu = menu.findItem(R.id.menu_recipe_add);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_recipe_add)
            startActivity(new Intent(this, RecipeMakeActivity.class));

        return super.onOptionsItemSelected(item);
    }

    void loadData() {
        compositeDisposable.add(
                recipeService.getRecipeByUserID(userID, "latest")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            recipeList = response.body();
                            recipeMyListAdapter.setNewData(recipeList);
                            recipeMyListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipelistRecycler.getParent());
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }

    void cancelLikeRecipe(int recipeid) {
        compositeDisposable.add(
                recipeService.setLikeRecipe(recipeid, userID, -1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            loadData();
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}
