package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.RecipeDetail;
import com.yhjoo.dochef.classes.Review;
import com.yhjoo.dochef.databinding.ARecipedetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends BaseActivity {
    ARecipedetailBinding binding;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현 (recipedetail, review)
        2. retrofit 구현
        3. 뷰들이 이상하다
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipedetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RetrofitServices.OverViewService overViewService = RetrofitBuilder.create(this,RetrofitServices.OverViewService.class);

        int recipdID = 1;
        overViewService.LoadOverViewCall(recipdID).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                try {
                    setheaderview(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void setheaderview(RecipeDetail recipeDetail) {
        try {
            ArrayList<Integer> recipies = new ArrayList<>(
                    Arrays.asList(R.drawable.tempimg_playrecipestart,
                    R.drawable.tempimg_playrecipe1,
                    R.drawable.tempimg_playrecipe2,
                    R.drawable.tempimg_playrecipe3,
                    R.drawable.tempimg_playrecipe4,
                    R.drawable.tempimg_playrecipefinish));

            binding.recipedetailRecipeimgs.setAdapter(new ImagePagerAdapter(RecipeDetailActivity.this, recipies));
            binding.recipedetailRecipeimgsIndicator.setViewPager(((ViewPager) findViewById(R.id.recipedetail_recipeimgs)));
            binding.recipedetailRecipetitle.setText(recipeDetail.getTitle());

            Glide.with(this)
                    .load("getString(R.string.profile_image_storage_url)" + recipeDetail.getProducerID())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.recipedetailUserimg);

            binding.recipedetailNickname.setText(recipeDetail.getProducerName());
            binding.recipedetailExplain.setText(recipeDetail.getSubstance());

            JSONArray tagsArray = new JSONArray(recipeDetail.getTag());

            for (int i = 0; i < tagsArray.length(); i++) {
                AppCompatTextView textView = new AppCompatTextView(this);
                textView.setText(tagsArray.getString(i));
                textView.setTextColor(getResources().getColor(R.color.colorPrimary,null));

                binding.recipedetailTags.addView(textView);
            }

            binding.recipedetailStartrecipe.setOnClickListener((v) ->
                    startActivity(new Intent(this, PlayRecipeActivity.class)));

            JSONArray aa = new JSONArray(recipeDetail.getIngredients());
            for (int i = 0; i < aa.length(); i++) {
                ViewGroup motherview = (ViewGroup) getLayoutInflater().inflate(R.layout.li_ingredient, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_product));
                view1.setText(aa.getJSONObject(i).getString("name"));
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_quantity));
                view2.setText(aa.getJSONObject(i).getString("amount"));

                binding.recipedetailIngredients.addView(motherview);
            }

            ArrayList<Review> bb = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

            binding.recipedetailReviewMore.setVisibility(bb.size() >= 2 ? View.VISIBLE : View.GONE);
            binding.recipedetailReviewMore.setOnClickListener((v) -> startActivity(new Intent(this, ReviewActivity.class)));

            ReviewListAdapter reviewListAdapter = new ReviewListAdapter();
            reviewListAdapter.setOnItemClickListener((baseQuickAdapter, view, i) ->
                    startActivity(new Intent(RecipeDetailActivity.this, ReviewActivity.class)));
            binding.recipedetailReviewRecycler.setLayoutManager(new LinearLayoutManager(this));
            binding.recipedetailReviewRecycler.setAdapter(reviewListAdapter);
            reviewListAdapter.setNewData(bb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class ImagePagerAdapter extends PagerAdapter {
        Context mContext;
        ArrayList<Integer> imgids;

        ImagePagerAdapter(Context context, ArrayList<Integer> imgids) {
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

    class ReviewListAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {
        ReviewListAdapter() {
            super(R.layout.li_reviewbrief);
        }

        @Override
        protected void convert(BaseViewHolder helper, Review item) {
            helper.setRating(R.id.reviewbrief_rating, item.getRating());
            helper.setText(R.id.reviewbrief_nickname, item.getNickname());
            helper.setText(R.id.reviewbrief_contents, item.getContents());
            helper.setText(R.id.reviewbrief_datetext, "1일전");
        }
    }
}
