package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.viewpagerindicator.CirclePageIndicator;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Comment;
import com.yhjoo.dochef.classes.RecipeListItem;
import com.yhjoo.dochef.classes.RecipeOverview;
import com.yhjoo.dochef.classes.Review;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.DummyMaker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailActivity extends BaseActivity {
    private final int RecipeID = 1;
    private RecipeOverview recipeOverview;

    /*
        TODO
        1. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recipedetail);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitServices.OverViewService overViewService = retrofit.create(RetrofitServices.OverViewService.class);

        overViewService.LoadOverViewCall(RecipeID).enqueue(new Callback<RecipeOverview>() {
            @Override
            public void onResponse(Call<RecipeOverview> call, Response<RecipeOverview> response1) {
                recipeOverview = response1.body();
                overViewService.LoadCommentCall(RecipeID)
                        .enqueue(new Callback<ArrayList<Comment>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                                try {
                                    setheaderview();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<RecipeOverview> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void setheaderview() {
        try {
            ((ViewPager) findViewById(R.id.recipedetail_recipeimgs)).setAdapter(new ImagePagerAdapter(RecipeDetailActivity.this,
                    new ArrayList<>(Arrays.asList(R.drawable.tempimg_playrecipestart, R.drawable.tempimg_playrecipe1, R.drawable.tempimg_playrecipe2, R.drawable.tempimg_playrecipe3, R.drawable.tempimg_playrecipe4, R.drawable.tempimg_playrecipefinish)), Glide.with(this)));
            ((CirclePageIndicator) findViewById(R.id.recipedetail_recipeimgs_indicator)).setViewPager(((ViewPager) findViewById(R.id.recipedetail_recipeimgs)));

            ((AppCompatTextView) findViewById(R.id.recipedetail_recipetitle)).setText(recipeOverview.getTitle());

            Glide.with(this)
                    .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + recipeOverview.getProducerID())
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) findViewById(R.id.recipedetail_userimg));

            ((AppCompatTextView) findViewById(R.id.recipedetail_nickname)).setText(recipeOverview.getProducerName());

            ((AppCompatTextView) findViewById(R.id.recipedetail_explain)).setText(recipeOverview.getSubstance());

            JSONArray tagsArray = new JSONArray(recipeOverview.getTag());

            for (int i = 0; i < tagsArray.length(); i++) {
                AppCompatTextView textView = new AppCompatTextView(this);
                textView.setText(tagsArray.getString(i));
                textView.setTextColor(getResources().getColor(R.color.colorPrimary,null));

                ((FlexboxLayout) findViewById(R.id.recipedetail_tags)).addView(textView);
            }

            findViewById(R.id.recipedetail_startrecipe).setOnClickListener((v) -> startActivity(new Intent(this, PlayRecipeActivity.class)));
//            findViewById(R.id.recipedetail_startrecipe).setOnClickListener((v) -> startActivity(new Intent(this, TempActivity.class)));

            ((FlexboxLayout) findViewById(R.id.recipedetail_ingredients)).removeAllViews();

            JSONArray aa = new JSONArray(recipeOverview.getIngredients());
            for (int i = 0; i < aa.length(); i++) {
                ViewGroup motherview = (ViewGroup) getLayoutInflater().inflate(R.layout.li_ingredient, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_product));
                view1.setText(aa.getJSONObject(i).getString("name"));
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_quantity));
                view2.setText(aa.getJSONObject(i).getString("amount"));

                ((FlexboxLayout) findViewById(R.id.recipedetail_ingredients)).addView(motherview);
            }

            ArrayList<Review> bb = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

            findViewById(R.id.recipedetail_review_more).setVisibility(bb.size() >= 2 ? View.VISIBLE : View.GONE);
            findViewById(R.id.recipedetail_review_more).setOnClickListener((v) -> startActivity(new Intent(this, ReviewActivity.class)));

            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(bb, Glide.with(this));
            reviewListAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> startActivity(new Intent(RecipeDetailActivity.this, ReviewActivity.class)));
            ((RecyclerView) findViewById(R.id.recipedetail_review_recycler)).setLayoutManager(new LinearLayoutManager(this));
            ((RecyclerView) findViewById(R.id.recipedetail_review_recycler)).setAdapter(reviewListAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            requestManager.load(imgids.get(position))
                    .apply(RequestOptions.centerCropTransform())
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
            requestManager.load(item.getRecipeImg())
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_recommend_recipeimg));
            helper.setText(R.id.li_recommend_title, item.getTitle());
            helper.setText(R.id.li_recommend_nickname, "By - " + item.getNickName());
        }
    }

    private class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
        private final RequestManager requestManager;

        ReviewListAdapter(@Nullable ArrayList<Review> data, RequestManager requestManager) {
            super(R.layout.li_reviewbrief, data);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Review item) {
            requestManager
                    .load(Integer.valueOf(item.getImageURL()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.reviewbrief_recipeimg));

            requestManager
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.reviewbrief_userimg));

            helper.setRating(R.id.reviewbrief_rating, item.getRating());
            helper.setText(R.id.reviewbrief_nickname, item.getNickname());
            helper.setText(R.id.reviewbrief_contents, item.getContents());
            helper.setText(R.id.reviewbrief_date, "1일전");
        }
    }
}
