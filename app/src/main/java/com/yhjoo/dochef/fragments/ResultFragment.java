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
import com.yhjoo.dochef.activities.BaseActivity;
import com.yhjoo.dochef.activities.HomeActivity;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.SearchActivity;
import com.yhjoo.dochef.adapter.SearchListAdapter;
import com.yhjoo.dochef.databinding.FResultBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.SearchResult;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    private final int VIEWHOLDER_AD = 0;
    private final int VIEWHOLDER_ITEM_USER = 1;
    private final int VIEWHOLDER_ITEM_RECIPE_NAME = 2;
    private final int VIEWHOLDER_ITEM_INGREDIENT = 3;
    private final int VIEWHOLDER_ITEM_TAG = 4;

    FResultBinding binding;
    RxRetrofitServices.UserService userService;
    RxRetrofitServices.RecipeService recipeService;
    SearchListAdapter searchListAdapter;

    String keyword;
    int type;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        type = getArguments().getInt("type");

        userService = RxRetrofitBuilder.create(getContext(), RxRetrofitServices.UserService.class);
        recipeService = RxRetrofitBuilder.create(getContext(), RxRetrofitServices.RecipeService.class);

        if (type == VIEWHOLDER_ITEM_USER)
            searchListAdapter = new SearchListAdapter(type, new ArrayList<>(), R.layout.li_follow);
        else
            searchListAdapter = new SearchListAdapter(type, new ArrayList<>(), R.layout.li_recipe_result);

        searchListAdapter.setEmptyView(R.layout.rv_search, (ViewGroup) binding.resultRecycler.getParent());
        searchListAdapter.setOnItemClickListener((adapter, view1, position) -> {
            switch (adapter.getItemViewType(position)) {
                case VIEWHOLDER_ITEM_RECIPE_NAME:
                case VIEWHOLDER_ITEM_INGREDIENT:
                case VIEWHOLDER_ITEM_TAG:
                    Intent intent = new Intent(getContext(), RecipeDetailActivity.class)
                            .putExtra("recipeID", ((Recipe) adapter.getData().get(position)).getRecipeID());
                    startActivity(intent);
                    break;
                case VIEWHOLDER_ITEM_USER:
                    Intent intent2 = new Intent(getContext(), HomeActivity.class)
                            .putExtra("userID", ((UserBrief) ((SearchResult) adapter.getData().get(position)).getContent()).getUserID());
                    Utils.log(((UserBrief) ((SearchResult) adapter.getData().get(position)).getContent()).getUserID());
                    startActivity(intent2);
                    break;
            }
        });
        binding.resultRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.resultRecycler.setAdapter(searchListAdapter);

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
            if (type == searchListAdapter.VIEWHOLDER_ITEM_USER) {
                ((BaseActivity) getActivity()).getCompositeDisposable().add(
                        userService.getUserByNickname(keyword)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    setUserItem(response.body());
                                    searchListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());

                                }, RxRetrofitBuilder.defaultConsumer())
                );
            } else {
                Single<Response<ArrayList<Recipe>>> selectedService;

                if (type == searchListAdapter.VIEWHOLDER_ITEM_RECIPE_NAME)
                    selectedService = recipeService.getRecipeByName(keyword, "popular");
                else if (type == searchListAdapter.VIEWHOLDER_ITEM_TAG)
                    selectedService = recipeService.getRecipeByTag(keyword, "popular");
                else
                    selectedService = recipeService.getRecipeByIngredient(keyword, "popular");

                ((BaseActivity) getActivity()).getCompositeDisposable().add(
                        selectedService
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    setRecipeItem(response.body());
                                    searchListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
                                }, RxRetrofitBuilder.defaultConsumer())
                );
            }
        } else {
            if (type == VIEWHOLDER_ITEM_USER) {
                ArrayList<UserBrief> userBriefs = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_BRIEF));
                setUserItem(userBriefs);
            } else {
                ArrayList<Recipe> recipes = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
                setRecipeItem(recipes);
            }
        }
    }


    void setRecipeItem(ArrayList<Recipe> recipes) {
        ArrayList<SearchResult> searchResults = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            if (i != 0 && i % 4 == 0)
                searchResults.add(new SearchResult<>(VIEWHOLDER_AD));
            searchResults.add(new SearchResult<>(type, recipes.get(i)));
        }

        searchListAdapter.setNewData(searchResults);
        searchListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
    }

    void setUserItem(ArrayList<UserBrief> userBriefs) {
        ArrayList<SearchResult> searchResults = new ArrayList<>();
        for (int i = 0; i < userBriefs.size(); i++) {
            if (i != 0 && i % 4 == 0)
                searchResults.add(new SearchResult<>(VIEWHOLDER_AD));
            searchResults.add(new SearchResult<>(VIEWHOLDER_ITEM_USER, userBriefs.get(i)));
        }

        searchListAdapter.setNewData(searchResults);
        searchListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
    }
}
