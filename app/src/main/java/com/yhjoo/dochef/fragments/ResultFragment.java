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
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.MultiItemResult;
import com.yhjoo.dochef.model.UserBreif;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    private final int VIEWHOLDER_AD = 0;
    private final int VIEWHOLDER_ITEM_RECIPE = 1;
    private final int VIEWHOLDER_ITEM_USER = 2;
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
        1. Recipe 서버 추가 및 기능 구현
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
                case VIEWHOLDER_ITEM_RECIPE:
                case VIEWHOLDER_ITEM_INGREDIENT:
                case VIEWHOLDER_ITEM_TAG:
                    Intent intent1 = new Intent(getContext(), RecipeDetailActivity.class);
                    startActivity(intent1);
                    break;
                case VIEWHOLDER_ITEM_USER:
                    Intent intent2 = new Intent(getContext(), HomeActivity.class);
                    intent2.putExtra("userID", ((UserBreif) ((MultiItemResult) adapter.getData().get(position)).getContent()).getUserID());
                    startActivity(intent2);
                    break;
            }
        });
        resultListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.resultRecycler.getParent());
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
            this.keyword = ((SearchActivity) getActivity()).getKeyword();
            loadList();
        }
    }

    void loadList() {
        ArrayList<Recipe> recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        switch (type) {
            case VIEWHOLDER_ITEM_RECIPE:
                ArrayList<MultiItemResult> multiItemResults = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    multiItemResults.add(new MultiItemResult<>(VIEWHOLDER_ITEM_RECIPE, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        multiItemResults.add(new MultiItemResult<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(multiItemResults);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
                break;
            case VIEWHOLDER_ITEM_USER:
                userService.getUserByNickname(keyword)
                        .enqueue(new BasicCallback<ArrayList<UserBreif>>(getContext()) {
                            @Override
                            public void onResponse(Call<ArrayList<UserBreif>> call, Response<ArrayList<UserBreif>> response) {
                                super.onResponse(call, response);
                                if (response.code() == 500) {
                                    App.getAppInstance().showToast("user list 가져오기 실패");
                                } else {
                                    App.getAppInstance().showToast("user list 가져오기 성공");

                                    ArrayList<UserBreif> userBreif = response.body();
                                    ArrayList<MultiItemResult> userListItem = new ArrayList<>();

                                    for (int i = 0; i < response.body().size(); i++) {
                                        if (i % 5 != 4)
                                            userListItem.add(new MultiItemResult<>(type, userBreif.get(i)));
                                        else {
                                            userListItem.add(new MultiItemResult<>(type, userBreif.get(i)));
                                            userListItem.add(new MultiItemResult<>(VIEWHOLDER_AD));
                                        }
                                    }

                                    resultListAdapter.setNewData(userListItem);
                                    resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
                                }
                            }
                        });
                break;
            case VIEWHOLDER_ITEM_INGREDIENT:
                ArrayList<MultiItemResult> items2WithAd = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    items2WithAd.add(new MultiItemResult<>(VIEWHOLDER_ITEM_INGREDIENT, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        items2WithAd.add(new MultiItemResult<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(items2WithAd);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
                break;

            case VIEWHOLDER_ITEM_TAG:
                ArrayList<MultiItemResult> items3WithAd = new ArrayList<>();

                for (int i = 0; i < recipes.size(); i++) {
                    items3WithAd.add(new MultiItemResult<>(VIEWHOLDER_ITEM_TAG, recipes.get(i)));

                    if (i != 0 && i % 4 == 0) {
                        items3WithAd.add(new MultiItemResult<>(VIEWHOLDER_AD));
                    }
                }

                resultListAdapter.setNewData(items3WithAd);
                resultListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.resultRecycler.getParent());
                break;
        }
    }
}
