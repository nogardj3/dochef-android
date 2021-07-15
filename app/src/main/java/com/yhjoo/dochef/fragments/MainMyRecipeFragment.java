package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.adapter.RecipeMultiAdapter;
import com.yhjoo.dochef.databinding.FMainMyrecipeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemRecipe;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_AD;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_ITEM;
import static com.yhjoo.dochef.adapter.RecipeMultiAdapter.VIEWHOLDER_PAGER;

public class MainMyRecipeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String[] recommend_tags;

    FMainMyrecipeBinding binding;

    RetrofitServices.RecipeService recipeService;
    RecipeMultiAdapter recipeMultiAdapter;

    ArrayList<MultiItemRecipe> recipeListItems = new ArrayList<>();
    String userID;

    /*
        TODO
        1. get recipe by userId sort by datetime desc
        like recipe 추가 -> 뷰 내거 아님 남거 추가

    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainMyrecipeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RetrofitBuilder.create(this.getContext(), RetrofitServices.RecipeService.class);

        binding.fMyrecipeSwipe.setOnRefreshListener(this);
        binding.fMyrecipeSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary, null));

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null), UserBrief.class);
        userID = userInfo.getUserID();

        // get recipe by recipe sort by datetime desc
        recipeMultiAdapter = new RecipeMultiAdapter(recipeListItems,recipeService);
        recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fMyrecipeRecycler.getParent());
        recipeMultiAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                Intent intent = new Intent(MainMyRecipeFragment.this.getContext(), RecipeDetailActivity.class);
                intent.putExtra("recipeID", recipeListItems.get(position).getContent().getRecipeID());
                startActivity(intent);
            }
        });
        binding.fMyrecipeRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.fMyrecipeRecycler.setAdapter(recipeMultiAdapter);

        recommend_tags = getResources().getStringArray(R.array.recommend_tags);
        Random r = new Random();

        if (App.isServerAlive()) {
            getRecipelist();
        } else {
            ArrayList<Recipe> temp = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE));

            for (int i = 0; i < temp.size(); i++) {
                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));

                int tt = i % 4;
                int ttt = i / 4 % 2;
                if (i != 0 && tt == 0) {
                    if (ttt == 0)
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER,
                                recommend_tags[r.nextInt(recommend_tags.length)]));
                    else
                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                }
            }

            recipeMultiAdapter.setNewData(recipeListItems);
        }
        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            getRecipelist();
        }, 1000);
    }

    void getRecipelist() {
        recipeService.getRecipeByUserID(userID,"latest")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this.getContext()) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            ArrayList<Recipe> temp = response.body();
                            Random r = new Random();

                            recipeListItems.clear();
                            for (int i = 0; i < temp.size(); i++) {
                                recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_ITEM, temp.get(i)));

                                int tt = i % 4;
                                int ttt = i / 4 % 2;
                                if (i != 0 && tt == 0) {
                                    if (ttt == 0)
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_PAGER,
                                                recommend_tags[r.nextInt(recommend_tags.length)]));
                                    else
                                        recipeListItems.add(new MultiItemRecipe(VIEWHOLDER_AD));
                                }
                            }

                            recipeMultiAdapter.setNewData(recipeListItems);
                            recipeMultiAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.fMyrecipeRecycler.getParent());
                            binding.fMyrecipeSwipe.setRefreshing(false);
                        }
                    }
                });
    }
}