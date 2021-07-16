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
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.SearchActivity;
import com.yhjoo.dochef.adapter.ResultListAdapter;
import com.yhjoo.dochef.databinding.FResultBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.MultiItemResult;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    private final int VIEWHOLDER_AD = 0;
    private final int VIEWHOLDER_ITEM_USER = 1;
    private final int VIEWHOLDER_ITEM_RECIPE_NAME = 2;
    private final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    private final int VIEWHOLDER_ITEM_TAG = 4;

    FResultBinding binding;
    RetrofitServices.UserService userService;
    RetrofitServices.RecipeService recipeService;
    ResultListAdapter resultListAdapter;

    String keyword;
    int type;

    /*
        TODO
        user - my인지 누군지 구분
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        type = getArguments().getInt("type");

        userService = RetrofitBuilder.create(getContext(), RetrofitServices.UserService.class);
        recipeService = RetrofitBuilder.create(getContext(), RetrofitServices.RecipeService.class);

        if (type == VIEWHOLDER_ITEM_USER)
            resultListAdapter = new ResultListAdapter(type, new ArrayList<>(), R.layout.li_user);
        else
            resultListAdapter = new ResultListAdapter(type, new ArrayList<>(), R.layout.li_recipe_result);

        resultListAdapter.setEmptyView(R.layout.rv_search, (ViewGroup) binding.resultRecycler.getParent());
        resultListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            switch (adapter.getItemViewType(position)) {
                case VIEWHOLDER_ITEM_RECIPE_NAME:
                case VIEWHOLDER_ITEM_INGREDIENT:
                case VIEWHOLDER_ITEM_TAG:
                    Intent intent = new Intent(getContext(), RecipeDetailActivity.class)
                        .putExtra("recipeID", ((Recipe)adapter.getData().get(position)).getRecipeID());
                    startActivity(intent);
                    break;
                case VIEWHOLDER_ITEM_USER:
                    Intent intent2 = new Intent(getContext(), HomeActivity.class)
                        .putExtra("userID", ((UserBrief) ((MultiItemResult) adapter.getData().get(position)).getContent()).getUserID());
                    startActivity(intent2);
                    break;
            }
        });
        binding.resultRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.resultRecycler.setAdapter(resultListAdapter);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() != null && ((SearchActivity) getActivity()).getKeyword() != null) {
                if (keyword != null && !keyword.equals(((SearchActivity) getActivity()).getKeyword())) {
                    search();
                } else if (keyword == null && ((SearchActivity) getActivity()).getKeyword() != null) {
                    search();
                }
            }
        }
    }

    public void search() {
        if (((SearchActivity) getActivity()).getKeyword() != null) {
            keyword = ((SearchActivity) getActivity()).getKeyword();
            loadList();
        }
    }

    void loadList() {
        if (App.isServerAlive()) {
            if(type== resultListAdapter.VIEWHOLDER_ITEM_USER){
                userService.getUserByNickname(keyword)
                        .enqueue(new BasicCallback<ArrayList<UserBrief>>(getContext()) {
                            @Override
                            public void onResponse(Call<ArrayList<UserBrief>> call, Response<ArrayList<UserBrief>> response) {
                                super.onResponse(call, response);
                                if (response.code() == 500) {
                                    App.getAppInstance().showToast("user list 가져오기 실패");
                                } else {
                                    setUserItem(response.body());
                                }
                            }
                        });
            }
            else if (type== resultListAdapter.VIEWHOLDER_ITEM_RECIPE_NAME){
                recipeService.getRecipeByName(keyword,"popular")
                        .enqueue(new BasicCallback<ArrayList<Recipe>>(getContext()) {
                            @Override
                            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                                super.onResponse(call, response);
                                if (response.code() == 500) {
                                    App.getAppInstance().showToast("user list 가져오기 실패");
                                } else {
                                    setRecipeItem(response.body());
                                }
                            }
                        });
            }
            else if (type== resultListAdapter.VIEWHOLDER_ITEM_TAG){
                recipeService.getRecipeByTag(keyword,"popular")
                        .enqueue(new BasicCallback<ArrayList<Recipe>>(getContext()) {
                            @Override
                            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                                super.onResponse(call, response);
                                if (response.code() == 500) {
                                    App.getAppInstance().showToast("user list 가져오기 실패");
                                } else {
                                    setRecipeItem(response.body());
                                }
                            }
                        });
            }
            else if (type== resultListAdapter.VIEWHOLDER_ITEM_INGREDIENT){
                recipeService.getRecipeByIngredient(keyword,"popular")
                        .enqueue(new BasicCallback<ArrayList<Recipe>>(getContext()) {
                            @Override
                            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                                super.onResponse(call, response);
                                if (response.code() == 500) {
                                    App.getAppInstance().showToast("user list 가져오기 실패");
                                } else {
                                    setRecipeItem(response.body());
                                }
                            }
                        });
            }
        } else{
            if(type == VIEWHOLDER_ITEM_USER){
                ArrayList<UserBrief> userBriefs = DataGenerator.make(getResources(), getResources().getInteger(R.integer.LOCAL_TYPE_USER_BRIEF));
                setUserItem(userBriefs);
            }
            else{
                ArrayList<Recipe> recipes = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
                setRecipeItem(recipes);
            }
        }
    }


    void setRecipeItem(ArrayList<Recipe> recipes) {
        ArrayList<MultiItemResult> multiItemResults = new ArrayList<>();
        if (type == VIEWHOLDER_ITEM_USER) {
        } else{
            for (int i = 0; i < recipes.size(); i++) {
                multiItemResults.add(new MultiItemResult<>(type, recipes.get(i)));

                if (i != 0 && i % 4 == 0)
                    multiItemResults.add(new MultiItemResult<>(VIEWHOLDER_AD));
            }
        }
    }

    void setUserItem(ArrayList<UserBrief> userBriefs){
        ArrayList<MultiItemResult> multiItemResults = new ArrayList<>();
        for (int i = 0; i < userBriefs.size(); i++) {
            multiItemResults.add(new MultiItemResult<>(VIEWHOLDER_ITEM_USER, userBriefs.get(i)));

            if (i != 0 && i % 4 == 0)
                multiItemResults.add(new MultiItemResult<>(VIEWHOLDER_AD));
        }

        resultListAdapter.setNewData(multiItemResults);
        resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
    }
}
