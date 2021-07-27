package com.yhjoo.dochef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.BaseActivity;
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.model.RecipePlay;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class PlayRecipeEndFragment extends Fragment {
    FPlayrecipeItemBinding binding;
    RxRetrofitServices.RecipeService recipeService;
    RxRetrofitServices.ReviewService reviewService;

    RecipePhase recipePhase;
    RecipeDetail recipeDetail;

    boolean is_like_this;
    String userID;
    /*
        TODO
        1. 분기 - end용 처리하기
        2. 리뷰작성 기능
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeService = RxRetrofitBuilder.create(getContext(), RxRetrofitServices.RecipeService.class);
        reviewService = RxRetrofitBuilder.create(getContext(), RxRetrofitServices.ReviewService.class);

        userID = Utils.getUserBrief(getContext()).getUserID();
        recipePhase = (RecipePhase) getArguments().getSerializable("item");
        recipeDetail = (RecipeDetail) getArguments().getSerializable("item2");

        is_like_this = recipeDetail.getLikes().contains(userID);

        loadData();
        return view;
    }

    void loadData(){
        ImageLoadUtil.loadRecipeImage(getContext(),recipePhase.getRecipe_img(),binding.playrecipeItemImg);

        Utils.log(recipePhase.toString());
        binding.playrecipeItemTips.removeAllViews();
        for (String text : recipePhase.getTips()) {
            AppCompatTextView tiptext = (AppCompatTextView) getLayoutInflater().inflate(R.layout.v_tip, null);
            tiptext.setText(text);
            binding.playrecipeItemTips.addView(tiptext);
        }

        binding.playrecipeItemIngredients.removeAllViews();
        for (Ingredient ingredient : recipePhase.getIngredients()) {
            ConstraintLayout ingredientContainer = (ConstraintLayout) getLayoutInflater().inflate(R.layout.v_ingredient, null);
            AppCompatTextView ingredientName = ingredientContainer.findViewById(R.id.v_ingredient_name);
            ingredientName.setText(ingredient.getName());
            AppCompatTextView ingredientAmount = ingredientContainer.findViewById(R.id.v_ingredient_amount);
            ingredientAmount.setText(ingredient.getAmount());
            binding.playrecipeItemIngredients.addView(ingredientContainer);
        }

        binding.playrecipeItemContents.setText(recipePhase.getContents());

        binding.playrecipeEndGroup.setVisibility(View.VISIBLE);

        if (is_like_this)
            binding.playrecipeEndLike.setImageResource(R.drawable.ic_favorite_red);
        else
            binding.playrecipeEndLike.setImageResource(R.drawable.ic_favorite_black);

        binding.playrecipeEndReviewOk.setOnClickListener(this::addReview);
    }

    void addReview(View v) {
        if (!binding.playrecipeEndReviewEdittext.getText().toString().equals("")) {
            ((BaseActivity) getActivity()).getCompositeDisposable().add(
                    reviewService.createReview(recipeDetail.getRecipeID(), userID,
                            binding.playrecipeEndReviewEdittext.getText().toString(),
                            (long) binding.playrecipeEndRating.getRating()
                            ,System.currentTimeMillis())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                App.getAppInstance().showToast("리뷰가 등록되었습니다.");
                                ((BaseActivity) getActivity()).finish();
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        } else
            App.getAppInstance().showToast("댓글을 입력 해 주세요");
    }

    void setLike() {
        int like = is_like_this ? -1 : 1;
        ((BaseActivity) getActivity()).getCompositeDisposable().add(
        recipeService.setLikeRecipe(recipeDetail.getRecipeID(), userID, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    is_like_this = !is_like_this;
                    loadData();
                }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}
