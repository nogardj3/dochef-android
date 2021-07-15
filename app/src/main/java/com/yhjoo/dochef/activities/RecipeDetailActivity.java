package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.ReviewListAdapter;
import com.yhjoo.dochef.databinding.ARecipedetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.GlideApp;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

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

    String userID;
    int recipeID;

    /*
        TODO
        like 추가
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipedetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);
        reviewService = RetrofitBuilder.create(this, RetrofitServices.ReviewService.class);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null), UserBrief.class);
        userID = userInfo.getUserID();

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
            addCount();
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
                            reviewListAdapter.setEmptyView(R.layout.rv_empty_review, (ViewGroup) binding.recipedetailReviewRecycler.getParent());
                        }
                    }
                });

    }

    void setTopView() {
        if (App.isServerAlive()) {
            StorageReference pathReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("recipe/" + recipeDetailInfo.getRecipeImg());

            GlideApp.with(this)
                    .load(pathReference)
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
        binding.recipedetailLikecount.setText(Integer.toString(recipeDetailInfo.getLikes().size()));
        binding.recipedetailViewcount.setText(Integer.toString(recipeDetailInfo.getView_count()));
        binding.recipedetailReviewRatingText.setText(Integer.toString(recipeDetailInfo.getRating()));
        binding.recipedetailReviewRating.setRating(recipeDetailInfo.getRating());

        if(recipeDetailInfo.getLikes().contains(userID) || recipeDetailInfo.getUserID().equals(userID))
            binding.recipedetailLike.setImageResource(R.drawable.ic_favorite_24dp);
        else
            binding.recipedetailLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        binding.recipedetailLike.setOnClickListener((v) ->{
                    if(!recipeDetailInfo.getUserID().equals(userID))
                        setLike();
                });
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

    void addCount(){
        recipeService.addCount(recipeID)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            Utils.log("count ok");
                        }
                    }
                });
    }

    void setLike(){
        int like = recipeDetailInfo.getLikes().contains(userID) ? -1 : 1;

        recipeService.setLikeRecipe(recipeID,userID,like)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            getRecipeDetail();
                            getReviewList();
                        }
                    }
                });
    }
}
