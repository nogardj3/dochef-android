package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.adapter.RecipeMultiAdapter;
import com.yhjoo.dochef.databinding.FMainMyrecipeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.RecipeBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_AD;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_ITEM;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_PAGER;

public class MainMyRecipeFragment extends Fragment {
    private final String[] aa = {"추천 메뉴", "#매운맛 #간단", "인기 메뉴", "초스피드 간단메뉴"};

    FMainMyrecipeBinding binding;

    RetrofitServices.RecipeService recipeService;
    RecipeMultiAdapter recipeMultiAdapter;

    ArrayList<MultiItemRecipe> recipeListItems = new ArrayList<>();

    /*
        TODO
        1. get recipe by userId sort by datetime desc
        1. Recipe 서버 추가 및 기능 구현

    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainMyrecipeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RetrofitBuilder.create(this.getContext(), RetrofitServices.RecipeService.class);

        // get recipe by recipe sort by datetime desc
        recipeMultiAdapter = new RecipeMultiAdapter(recipeListItems);
        recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fMyrecipeRecycler.getParent());
        recipeMultiAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                startActivity(new Intent(getContext(), RecipeDetailActivity.class));
            }
        });
        binding.fMyrecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fMyrecipeRecycler.setAdapter(recipeMultiAdapter);

        if(App.isServerAlive()){
            getRecipelist();
        }
        else{
            ArrayList<RecipeBrief> temp = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE_BRIEF));

            for (int i = 0; i < temp.size(); i++) {
                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));

                int tt = i % 4;
                int ttt = i / 4 % 2;
                if (i != 0 && tt == 0) {
                    if (ttt == 0)
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER, aa[i % 4]));
                    else
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                }
            }

            recipeMultiAdapter.setNewData(recipeListItems);
        }
        return view;
    }

    void getRecipelist(){
        recipeService.getRecipeByTag("매운맛")
                .enqueue(new BasicCallback<ArrayList<RecipeBrief>>(this.getContext()) {
                    @Override
                    public void onResponse(Call<ArrayList<RecipeBrief>> call, Response<ArrayList<RecipeBrief>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<RecipeBrief> temp = response.body();

                            for (int i = 0; i < temp.size(); i++) {
                                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));

                                int tt = i % 4;
                                int ttt = i / 4 % 2;
                                if (i != 0 && tt == 0) {
                                    if (ttt == 0)
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER, aa[i % 4]));
                                    else
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                                }
                            }

                            recipeMultiAdapter.setNewData(recipeListItems);
                        }
                    }
                });
    }
}
