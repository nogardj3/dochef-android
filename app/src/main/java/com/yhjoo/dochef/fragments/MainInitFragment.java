package com.yhjoo.dochef.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
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
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FMainInitBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ArrayList<Integer> imgs = new ArrayList<>();
        imgs.add(R.drawable.ad_temp_0);
        imgs.add(R.drawable.ad_temp_1);

        binding.mainAdviewpager.setAdapter(new ImagePagerAdapter(MainInitFragment.this.getContext(), imgs));
        binding.mainAdviewpagerIndicator.setViewPager(binding.mainAdviewpager);
        binding.mainRecommendMore.setOnClickListener(
                v -> startActivity(new Intent(MainInitFragment.this.getActivity(), RecipeThemeActivity.class)));

        Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> binding.mainAdviewpager
                        .setCurrentItem(binding.mainAdviewpager.getCurrentItem() == imgs.size() - 1
                                ? 0 : binding.mainAdviewpager.getCurrentItem() + 1));

        if(App.isServerAlive()){}
        else
            recipes = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        RecommendAdapter recommendAdapter = new RecommendAdapter();
        recommendAdapter.setOnItemClickListener((adapter, view1, position) -> startActivity(new Intent(MainInitFragment.this.getActivity(), RecipeDetailActivity.class)));
        recommendAdapter.setNewData(recipes);
        binding.mainRecommendRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.mainRecommendRecyclerview.setAdapter(recommendAdapter);

        return view;
    }

    class ImagePagerAdapter extends PagerAdapter {
        Context mContext;
        ArrayList<Integer> imgids;

        public ImagePagerAdapter(Context context, ArrayList<Integer> imgids) {
            this.mContext = context;
            this.imgids = imgids;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            AppCompatImageView aa = new AppCompatImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            aa.setLayoutParams(lp);
            Glide.with(mContext)
                    .load(imgids.get(position))
                    .apply(RequestOptions.centerInsideTransform())
                    .into(aa);

            collection.addView(aa);

            return aa;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return imgids.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    class RecommendAdapter extends BaseQuickAdapter<Recipe, BaseViewHolder> {
        RecommendAdapter() {
            super(R.layout.li_recipe_recommend);
        }

        @Override
        protected void convert(BaseViewHolder helper, Recipe item) {
            if(App.isServerAlive())
                Glide.with(mContext)
                        .load(item.getRecipeImg())
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));
            else
                Glide.with(mContext)
                        .load(Integer.parseInt(item.getRecipeImg()))
                        .apply(RequestOptions.centerCropTransform())
                        .into((AppCompatImageView) helper.getView(R.id.reciperecommend_recipeimg));

            helper.setText(R.id.reciperecommend_title, item.getTitle());
            helper.setText(R.id.reciperecommend_nickname, "By - " + item.getNickName());
        }
    }
}