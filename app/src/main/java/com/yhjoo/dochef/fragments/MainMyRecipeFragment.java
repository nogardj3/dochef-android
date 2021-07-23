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
import com.yhjoo.dochef.databinding.FMainMyrecipeBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_AD;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_ITEM;

public class MainMyRecipeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    FMainMyrecipeBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RecipeMultiAdapter recipeMultiAdapter;

    ArrayList<MultiItemRecipe> recipeListItems = new ArrayList<>();
    String[] recommend_tags;
    String userID;

    /*
        TODO
        Recommend multi adapter 변경
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainMyrecipeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RxRetrofitBuilder.create(this.getContext(),
                RxRetrofitServices.RecipeService.class);

        binding.fMyrecipeSwipe.setOnRefreshListener(this);
        binding.fMyrecipeSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));

        userID = Utils.getUserBrief(this.getContext()).getUserID();

        recipeMultiAdapter = new RecipeMultiAdapter(recipeListItems);
        recipeMultiAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.fMyrecipeRecycler.getParent());
        recipeMultiAdapter.setUserid(userID);
        recipeMultiAdapter.setShowYours(true);
        recipeMultiAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                Intent intent = new Intent(MainMyRecipeFragment.this.getContext(), RecipeDetailActivity.class)
                        .putExtra("recipeID", recipeListItems.get(position).getContent().getRecipeID());
                startActivity(intent);
            }
        });
        binding.fMyrecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fMyrecipeRecycler.setAdapter(recipeMultiAdapter);

        recommend_tags = getResources().getStringArray(R.array.recommend_tags);


        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(this::getRecipelist, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.isServerAlive())
            getRecipelist();
        else {
            ArrayList<Recipe> temp = DataGenerator.make(getResources(),
                    getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
            Random r = new Random();

            for (int i = 0; i < temp.size(); i++) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));
            }

            recipeMultiAdapter.setNewData(recipeListItems);
        }
    }

    void getRecipelist() {
        ((BaseActivity) getActivity()).getCompositeDisposable().add(
                recipeService.getRecipeByUserID(userID, "latest")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            ArrayList<Recipe> temp = response.body();
                            Random r = new Random();

                            recipeListItems.clear();
                            for (int i = 0; i < temp.size(); i++) {
                                if (i != 0 && i % 4 == 0)
                                    recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));
                            }

                            recipeMultiAdapter.setNewData(recipeListItems);
                            recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fMyrecipeRecycler.getParent());
                            binding.fMyrecipeSwipe.setRefreshing(false);
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}