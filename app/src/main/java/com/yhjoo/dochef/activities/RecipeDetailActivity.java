package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.ReviewListAdapter;
import com.yhjoo.dochef.databinding.ARecipedetailBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Response;

public class RecipeDetailActivity extends BaseActivity {
    ARecipedetailBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RxRetrofitServices.ReviewService reviewService;
    ReviewListAdapter reviewListAdapter;

    ArrayList<Review> reviewList;
    RecipeDetail recipeDetailInfo;
    String userID;
    int recipeID;

    /*
        TODO
        recipe revise, delete 추가
        review userdetail 넘어가기 확인
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipedetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeService = RxRetrofitBuilder.create(this, RxRetrofitServices.RecipeService.class);
        reviewService = RxRetrofitBuilder.create(this, RxRetrofitServices.ReviewService.class);

        userID = Utils.getUserBrief(this).getUserID();
        recipeID = getIntent().getIntExtra("recipeID", 0);

        reviewListAdapter = new ReviewListAdapter();
        reviewListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            Intent intent = new Intent(RecipeDetailActivity.this, HomeActivity.class)
                    .putExtra("userID", ((Review) adapter.getData().get(position)).getUserID());
            startActivity(intent);
        });
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

        addCount();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            addCount();
            loadData();
        } else {
            recipeDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL));
            reviewList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_REVIEW));

            setTopView();
            reviewListAdapter.setNewData(reviewList);
        }
    }

    void loadData() {
        compositeDisposable.add(
                recipeService.getRecipeDetail(recipeID)
                        .flatMap((Function<Response<RecipeDetail>, Single<Response<ArrayList<Review>>>>)
                                response -> {
                                    recipeDetailInfo = response.body();
                                    return reviewService.getReview(recipeID)
                                            .observeOn(AndroidSchedulers.mainThread());
                                }
                        )
                        .subscribe(response -> {
                            reviewList = response.body();

                            setTopView();
                            reviewListAdapter.setNewData(reviewList);
                            reviewListAdapter.setEmptyView(R.layout.rv_empty_review,
                                    (ViewGroup) binding.recipedetailReviewRecycler.getParent());
                        }, RxRetrofitBuilder.defaultConsumer())

        );
    }

    void setTopView() {
        ImageLoadUtil.loadRecipeImage(this, recipeDetailInfo.getRecipeImg(), binding.recipedetailMainImg);
        ImageLoadUtil.loadUserImage(this, recipeDetailInfo.getUserImg(), binding.recipedetailUserimg);

        binding.recipedetailRecipetitle.setText(recipeDetailInfo.getRecipeName());
        binding.recipedetailNickname.setText(recipeDetailInfo.getNickname());
        binding.recipedetailExplain.setText(recipeDetailInfo.getContents());
        binding.recipedetailLikecount.setText(Integer.toString(recipeDetailInfo.getLikes().size()));
        binding.recipedetailViewcount.setText(Integer.toString(recipeDetailInfo.getView_count()));
        binding.recipedetailReviewRatingText.setText(Integer.toString(recipeDetailInfo.getRating()));
        binding.recipedetailReviewRating.setRating(recipeDetailInfo.getRating());

        if (recipeDetailInfo.getLikes().contains(userID) || recipeDetailInfo.getUserID().equals(userID))
            binding.recipedetailLike.setImageResource(R.drawable.ic_favorite_red);
        else
            binding.recipedetailLike.setImageResource(R.drawable.ic_favorite_black);
        binding.recipedetailLike.setOnClickListener((v) -> {
            if (!recipeDetailInfo.getUserID().equals(userID))
                setLike();
        });
        binding.recipedetailStartrecipe.setOnClickListener((v) ->
                startActivity(new Intent(this, PlayRecipeActivity.class)));
        binding.recipedetailUserWrapper.setOnClickListener((v) -> {
            Intent intent = new Intent(this, HomeActivity.class)
                    .putExtra("userID", recipeDetailInfo.getUserID());
            startActivity(intent);
        });

        binding.recipedetailTags.removeAllViews();
        for (String tag : recipeDetailInfo.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) getLayoutInflater().inflate(R.layout.v_tag_recipe, null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_recipe_text);
            tagview.setText("#" + tag);
            binding.recipedetailTags.addView(tagcontainer);
        }

        binding.recipedetailIngredients.removeAllViews();
        for (Ingredient ingredient : recipeDetailInfo.getIngredients()) {
            ConstraintLayout ingredientContainer = (ConstraintLayout) getLayoutInflater().inflate(R.layout.v_ingredient, null);
            AppCompatTextView ingredientName = ingredientContainer.findViewById(R.id.v_ingredient_name);
            ingredientName.setText(ingredient.getName());
            AppCompatTextView ingredientAmount = ingredientContainer.findViewById(R.id.v_ingredient_amount);
            ingredientAmount.setText(ingredient.getAmount());
            binding.recipedetailIngredients.addView(ingredientContainer);
        }
    }

    void addCount() {
        compositeDisposable.add(
                recipeService.addCount(recipeID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> Utils.log("count ok"),
                                RxRetrofitBuilder.defaultConsumer())
        );
    }

    void setLike() {
        int like = recipeDetailInfo.getLikes().contains(userID) ? -1 : 1;
        compositeDisposable.add(
                recipeService.setLikeRecipe(recipeID, userID, like)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> loadData(), RxRetrofitBuilder.defaultConsumer())
        );
    }
}
