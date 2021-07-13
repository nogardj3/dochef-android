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
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class MainInitFragment extends Fragment {
    FMainInitBinding binding;

    ArrayList<Recipe> recipes;

    /*
        TODO
        1. Recipe 서버 추가 및 기능 구현
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainInitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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

        if(App.isServerAlive()){
            // get recipes
        }
        else
            recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        RecommendAdapter recommendAdapter = new RecommendAdapter();
        recommendAdapter.setOnItemClickListener((adapter, view1, position) -> startActivity(new Intent(getContext(), RecipeDetailActivity.class)));
        recommendAdapter.setNewData(recipes);
        binding.mainRecommendRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.mainRecommendRecyclerview.setAdapter(recommendAdapter);

        return view;
    }
}