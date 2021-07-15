package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.ReviewListAdapter;
import com.yhjoo.dochef.databinding.ARecipedetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeDetailActivity extends BaseActivity {
    ARecipedetailBinding binding;

    ReviewListAdapter reviewListAdapter;

    RetrofitServices.RecipeService recipeService;
    RetrofitServices.ReviewService reviewService;

    RecipeDetail recipeDetailInfo;
    ArrayList<Review> reviewList;

    int recipeID;

    /*
        TODO
        1. 실행 해보고 수정할거 수정하기
        - like 추가
        - tag 수정
        - ingredients 수정
        - share 수정
        - 스크롤문제
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipedetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);
        reviewService = RetrofitBuilder.create(this, RetrofitServices.ReviewService.class);

        recipeID = getIntent().getIntExtra("recipeID", 0);

        reviewListAdapter = new ReviewListAdapter();
        binding.recipedetailReviewRecycler.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recipedetailReviewRecycler.setAdapter(reviewListAdapter);

        if (App.isServerAlive()) {
            getRecipeDetail();
            getReviewList();
        } else {
            recipeDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_RECIPE_DETAIL));
            reviewList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

            setTopView();
            reviewListAdapter.setNewData(reviewList);
        }
    }

    void getRecipeDetail() {
        recipeService.getRecipeDetail(recipeID)
                .enqueue(new BasicCallback<RecipeDetail>(this) {
                    @Override
                    public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            recipeDetailInfo = response.body();
                            setTopView();
                        }
                    }
                });
    }

    void getReviewList() {
        reviewService.getReview(recipeID)
                .enqueue(new BasicCallback<ArrayList<Review>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            reviewList = response.body();
                            reviewListAdapter.setNewData(reviewList);
                            reviewListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.recipedetailReviewRecycler.getParent());
                        }
                    }
                });

    }

    void setTopView() {
        if (App.isServerAlive()) {
            Glide.with(this)
                    .load(getString(R.string.storage_image_url_recipe) + recipeDetailInfo.getRecipeImg())
                    .centerCrop()
                    .into(binding.recipedetailMainImg);
            if (!recipeDetailInfo.getUserImg().equals("default"))
                Glide.with(this)
                        .load(getString(R.string.storage_image_url_profile) + recipeDetailInfo.getRecipeImg())
                        .circleCrop()
                        .into(binding.recipedetailUserimg);
        } else {
            Glide.with(this)
                    .load(Integer.parseInt(recipeDetailInfo.getRecipeImg()))
                    .centerCrop()
                    .into(binding.recipedetailMainImg);
            Glide.with(this)
                    .load(Integer.parseInt(recipeDetailInfo.getUserImg()))
                    .circleCrop()
                    .into(binding.recipedetailUserimg);
        }


        binding.recipedetailRecipetitle.setText(recipeDetailInfo.getRecipeName());
        binding.recipedetailNickname.setText(recipeDetailInfo.getNickname());
        binding.recipedetailExplain.setText(recipeDetailInfo.getContents());
//        binding.recipedetailLikecount.setText(Integer.toString(recipeDetailInfo.getView_count()));
        binding.recipedetailViewcount.setText(Integer.toString(recipeDetailInfo.getView_count()));
        binding.recipedetailReviewRatingText.setText(Integer.toString(recipeDetailInfo.getRating()));
        binding.recipedetailReviewRating.setRating(recipeDetailInfo.getRating());

//        binding.recipedetailLike.setOnClickListener((v) ->
//                startActivity(new Intent(this, PlayRecipeActivity.class)));
//        binding.recipedetailShare.setOnClickListener((v) ->
//                startActivity(new Intent(this, PlayRecipeActivity.class)));
        binding.recipedetailStartrecipe.setOnClickListener((v) ->
                startActivity(new Intent(this, PlayRecipeActivity.class)));

        binding.recipedetailTags.removeAllViews();
        for (String tag : recipeDetailInfo.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) getLayoutInflater().inflate(R.layout.v_tag_recipe,null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_recipe_text);
            tagview.setText("#" + tag);
            binding.recipedetailTags.addView(tagcontainer);
        }

        binding.recipedetailIngredients.removeAllViews();
        for (Ingredient ingredient : recipeDetailInfo.getIngredients()) {
            ConstraintLayout ingredientContainer = (ConstraintLayout) getLayoutInflater().inflate(R.layout.v_ingredient,null);
            AppCompatTextView ingredientName = ingredientContainer.findViewById(R.id.v_ingredient_name);
            ingredientName.setText(ingredient.getName());
            AppCompatTextView ingredientAmount = ingredientContainer.findViewById(R.id.v_ingredient_amount);
            ingredientAmount.setText(ingredient.getAmount());
            binding.recipedetailIngredients.addView(ingredientContainer);
        }
    }
}
