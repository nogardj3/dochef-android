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
import com.yhjoo.dochef.activities.BaseActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.adapter.RecipeMultiAdapter;
import com.yhjoo.dochef.databinding.FMainRecipesBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;

import java.util.ArrayList;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_AD;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_ITEM;

public class MainRecipesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public enum SORT {LATEST, POPULAR, RATING}

    FMainRecipesBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RecipeMultiAdapter recipeMultiAdapter;

    ArrayList<MultiItemRecipe> recipeListItems = new ArrayList<>();
    String[] recommend_tags;
    SORT currentMode = SORT.LATEST;

    /*
        TODO
        multiadapter
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainRecipesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RxRetrofitBuilder.create(this.getContext(),
                RxRetrofitServices.RecipeService.class);

        binding.fRecipeSwipe.setOnRefreshListener(this);
        binding.fRecipeSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));
        recipeMultiAdapter = new RecipeMultiAdapter(recipeListItems);
        recipeMultiAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.fRecipeRecycler.getParent());
        recipeMultiAdapter.setShowNew(true);
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

        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> getRecipeList(currentMode),1000);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (App.isServerAlive())
            getRecipeList(currentMode);
        else {
            ArrayList<Recipe> temp = DataGenerator.make(getResources(),
                    getResources().getInteger(R.integer.DATE_TYPE_RECIPE));

            for (int i = 0; i < temp.size(); i++) {
                if (i != 0 && i % 4 == 0) {
                    if (i / 4 % 2 == 0)
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                }
                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));
            }
        }
    }

    void getRecipeList(SORT sort) {
        String sortmode = "";
        if(sort == SORT.LATEST)
            sortmode = "latest";
        else if(sort == SORT.POPULAR)
            sortmode = "popular";
        else if(sort == SORT.RATING)
            sortmode = "rating";

        ((BaseActivity) getActivity()).getCompositeDisposable().add(
                recipeService.getRecipes(sortmode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            ArrayList<Recipe> arrayList = response.body();
                            Random r = new Random();

                            recipeListItems.clear();
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i != 0 && i % 4 == 0) {
                                    recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                                }
                                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, arrayList.get(i)));
                            }

                            recipeMultiAdapter.setNewData(recipeListItems);
                            recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fRecipeRecycler.getParent());
                            binding.fRecipeRecycler.getLayoutManager().scrollToPosition(0);
                            binding.fRecipeSwipe.setRefreshing(false);
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }

    public void changeSortMode(SORT sort) {
        if (currentMode != sort) {
            recipeMultiAdapter.setNewData(new ArrayList<>());
            recipeMultiAdapter.notifyDataSetChanged();
            recipeMultiAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.fRecipeRecycler.getParent());

            currentMode = sort;
            getRecipeList(currentMode);
        }
    }
}