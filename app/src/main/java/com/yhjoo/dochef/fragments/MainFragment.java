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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.viewpagerindicator.CirclePageIndicator;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.RecipeDetailActivity;
import com.yhjoo.dochef.activities.RecipeThemeActivity;
import com.yhjoo.dochef.classes.RecipeListItem;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class MainFragment extends Fragment {
    @BindView(R.id.main_adviewpager)
    ViewPager viewPager;

    /*
        TODO
        1. 밑에 뭘 넣으면 좋을까
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_main, container, false);
        ButterKnife.bind(this, view);

        ArrayList<Integer> imgs = new ArrayList<>();
        imgs.add(R.drawable.ad_temp_0);
        imgs.add(R.drawable.ad_temp_1);

        viewPager.setAdapter(new ImagePagerAdapter(MainFragment.this.getContext(), imgs, Glide.with(getContext())));
        ((CirclePageIndicator) view.findViewById(R.id.main_adviewpager_indicator)).setViewPager(((ViewPager) viewPager));

        Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> viewPager.setCurrentItem(viewPager.getCurrentItem() == imgs.size() - 1 ? 0 : viewPager.getCurrentItem() + 1));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_recommend_recyclerview);

        ArrayList<RecipeListItem> recipeListItems = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPIES));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RecommendAdapter recommendAdapter = new RecommendAdapter(recipeListItems, Glide.with(getContext()));
        recyclerView.setAdapter(recommendAdapter);
        recommendAdapter.setOnItemClickListener((adapter, view1, position) -> startActivity(new Intent(MainFragment.this.getActivity(), RecipeDetailActivity.class)));
        view.findViewById(R.id.main_recommend_more).setOnClickListener(v -> startActivity(new Intent(MainFragment.this.getActivity(), RecipeThemeActivity.class)));

        return view;
    }

    public class ImagePagerAdapter extends PagerAdapter {
        private final Context mContext;
        private final ArrayList<Integer> imgids;
        private final RequestManager requestManager;

        public ImagePagerAdapter(Context context, ArrayList<Integer> imgids, RequestManager requestManager) {
            this.mContext = context;
            this.imgids = imgids;
            this.requestManager = requestManager;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            AppCompatImageView aa = new AppCompatImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            aa.setLayoutParams(lp);
            requestManager
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

    private class RecommendAdapter extends BaseQuickAdapter<RecipeListItem, BaseViewHolder> {
        private final RequestManager requestManager;

        RecommendAdapter(ArrayList<RecipeListItem> recipeListItem, RequestManager requestManager) {
            super(R.layout.li_recommend, recipeListItem);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, RecipeListItem item) {
            requestManager
                    .load(item.getRecipeImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
            helper.setText(R.id.li_recommend_title, item.getTitle());
            helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
        }
    }
}