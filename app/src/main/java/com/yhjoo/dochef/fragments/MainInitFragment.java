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
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.adapter.MainAdPagerAdapter;
import com.yhjoo.dochef.adapter.RecommendAdapter;
import com.yhjoo.dochef.databinding.FMainInitBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Response;

public class MainInitFragment extends Fragment {
    FMainInitBinding binding;

    RetrofitServices.RecipeService recipeService;
    RecommendAdapter recommendAdapter;
    ArrayList<Recipe> recipeList;

    /*
        TODO
        1. 실행 해보고 수정할거 수정하기
        recommendAdapter emptyview 필요없음
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainInitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RetrofitBuilder.create(this.getContext(), RetrofitServices.RecipeService.class);

        ArrayList<Integer> imgs = new ArrayList<>();
        imgs.add(R.drawable.ad_temp_0);
        imgs.add(R.drawable.ad_temp_1);


        binding.mainAdviewpager.setAdapter(new MainAdPagerAdapter(getContext(), imgs));
        binding.mainAdviewpagerIndicator.setViewPager(binding.mainAdviewpager);
        binding.mainRecommendMore.setOnClickListener(
                v -> startActivity(new Intent(getContext(), RecipeThemeActivity.class)));

        Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> binding.mainAdviewpager
                        .setCurrentItem(binding.mainAdviewpager.getCurrentItem() == imgs.size() - 1
                                ? 0 : binding.mainAdviewpager.getCurrentItem() + 1));


        recommendAdapter = new RecommendAdapter();
        recommendAdapter.setOnItemClickListener((adapter, view1, position) -> startActivity(new Intent(getContext(), RecipeDetailActivity.class)));
        recommendAdapter.setNewData(recipeList);
        binding.mainRecommendRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.mainRecommendRecyclerview.setAdapter(recommendAdapter);
        if (App.isServerAlive()) {
            getRecipelist();
        } else {
            recipeList = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE));
            recommendAdapter.setNewData(recipeList);
        }

        return view;
    }

    void getRecipelist() {
        recipeService.getRecipeByTag("매운맛")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this.getContext()) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            recipeList = response.body();
                            recommendAdapter.setNewData(recipeList);
                            recommendAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.mainRecommendRecyclerview.getParent());
                        }
                    }
                });
    }
}