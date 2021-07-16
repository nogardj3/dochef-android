package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.adapter.RecipeMultiAdapter;
import com.yhjoo.dochef.databinding.FMainRecipesBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Response;

import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_AD;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_ITEM;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_PAGER;

public class MainRecipesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int mode_Recent = 1;
    public static final int mode_Popular = 2;

    FMainRecipesBinding binding;
    RetrofitServices.RecipeService recipeService;
    RecipeMultiAdapter recipeMultiAdapter;

    ArrayList<MultiItemRecipe> recipeListItems = new ArrayList<>();
    String[] recommend_tags;
    int currentMode = 1;

    /*
        TODO
        인기레시피 -> 분류별 레시피
            별점순 = rating, view_count    sort by rating
            조회순 = rating, view_count    sort by view_count
            최신순 = rating, datetime      sort by latest
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainRecipesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RetrofitBuilder.create(this.getContext(), RetrofitServices.RecipeService.class);

        binding.fRecipeSwipe.setOnRefreshListener(this);
        binding.fRecipeSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        recipeMultiAdapter = new RecipeMultiAdapter(recipeListItems, recipeService);
        recipeMultiAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                Intent intent = new Intent(MainRecipesFragment.this.getContext(), RecipeDetailActivity.class)
                        .putExtra("recipeID", ((MultiItemRecipe) adapter.getData().get(position)).getContent().getRecipeID());
                startActivity(intent);
            }
        });
        binding.fRecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fRecipeRecycler.setAdapter(recipeMultiAdapter);

        recommend_tags = getResources().getStringArray(R.array.recommend_tags);
        Random r = new Random();

        if (App.isServerAlive())
            getRecipeList();
        else {
            ArrayList<Recipe> temp = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));

            for (int i = 0; i < temp.size(); i++) {
                if (i != 0 && i % 4 == 0) {
                    if (i / 4 % 2 == 0)
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER,
                                recommend_tags[r.nextInt(recommend_tags.length)]));
                    else
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                }
                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));
            }
        }

        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(this::getRecipeList, 1000);
    }

    void getRecipeList() {
        recipeService.getRecipes("popular")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this.getContext()) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<Recipe> arrayList = response.body();
                            Random r = new Random();

                            recipeListItems.clear();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i != 0 && i % 4 == 0) {
                                    if (i / 4 % 2 == 0)
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER,
                                                recommend_tags[r.nextInt(recommend_tags.length)]));
                                    else
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                                }
                                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, arrayList.get(i)));
                            }

                            recipeMultiAdapter.setNewData(recipeListItems);
                            recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fRecipeRecycler.getParent());
                            binding.fRecipeSwipe.setRefreshing(false);
                        }
                    }
                });
    }

    public int getAlignMode() {
        return currentMode;
    }

    public void changeAlignMode() {
        Utils.log("change align mode");
        if (currentMode == mode_Recent) {
            currentMode = mode_Popular;
            recipeMultiAdapter.setNewData(new ArrayList<>());
            recipeMultiAdapter.notifyDataSetChanged();
//            recipeMultiAdapter.setEmptyView(R.layout.rv_loading);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        App.getAppInstance().showToast("인기순");
                        recipeMultiAdapter.setNewData(recipeListItems);
                        recipeMultiAdapter.notifyDataSetChanged();
                        binding.fRecipeRecycler.getLayoutManager().scrollToPosition(0);
                    });
        } else if (currentMode == mode_Popular) {
            currentMode = mode_Recent;
            recipeMultiAdapter.setNewData(new ArrayList<>());
            recipeMultiAdapter.notifyDataSetChanged();
//            recipeMultiAdapter.setEmptyView(R.layout.rv_loading);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        App.getAppInstance().showToast("최신순");
                        recipeMultiAdapter.setNewData(recipeListItems);
                        recipeMultiAdapter.notifyDataSetChanged();
                        binding.fRecipeRecycler.getLayoutManager().scrollToPosition(0);
                    });
        }
    }
}