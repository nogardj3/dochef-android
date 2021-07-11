package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.ReviewListAdapter;
import com.yhjoo.dochef.databinding.ARecipedetailBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends BaseActivity {
    ARecipedetailBinding binding;

    /*
        TODO
        1. Review 뷰 추가
        2. Recipe 서버 추가 및 기능 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipedetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RetrofitServices.OverViewService overViewService = RetrofitBuilder.create(this, RetrofitServices.OverViewService.class);

        int recipdID = 1;
        overViewService.LoadOverViewCall(recipdID).enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                try {
                    setTopView(response.body());
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

    void setTopView(RecipeDetail recipeDetail) {
        try {
//            if (!recipeDetail.getMainImg().equals("default")) {
//                Glide.with(this)
//                        .load(getString(R.string.storage_image_url_post) + recipeDetail.getMainImg())
//                        .apply(RequestOptions.centerCropTransform())
//                        .into(binding.recipedetailMainImg);
//            }

            binding.recipedetailRecipetitle.setText(recipeDetail.getTitle());

            Glide.with(this)
                    .load(getString(R.string.storage_image_url_profile) + recipeDetail.getProducerID())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.recipedetailUserimg);

            binding.recipedetailNickname.setText(recipeDetail.getProducerName());
            binding.recipedetailExplain.setText(recipeDetail.getSubstance());

            JSONArray tagsArray = new JSONArray(recipeDetail.getTag());

            for (int i = 0; i < tagsArray.length(); i++) {
                AppCompatTextView textView = (AppCompatTextView) getLayoutInflater().inflate(R.layout.v_tag,null);
                textView.setText("#" + tagsArray.getString(i) + " ");

                binding.recipedetailTags.addView(textView);
            }

            binding.recipedetailStartrecipe.setOnClickListener((v) ->
                    startActivity(new Intent(this, PlayRecipeActivity.class)));

            JSONArray aa = new JSONArray(recipeDetail.getIngredients());
            for (int i = 0; i < aa.length(); i++) {
                ViewGroup motherview = (ViewGroup) getLayoutInflater().inflate(R.layout.li_ingredient, null);
                AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.ingredient_product));
                view1.setText(aa.getJSONObject(i).getString("name"));
                AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.ingredient_quantity));
                view2.setText(aa.getJSONObject(i).getString("amount"));

                binding.recipedetailIngredients.addView(motherview);
            }

            ArrayList<Review> bb = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_REVIEW));

            ReviewListAdapter reviewListAdapter = new ReviewListAdapter();
            binding.recipedetailReviewRecycler.setLayoutManager(new LinearLayoutManager(this));
            binding.recipedetailReviewRecycler.setAdapter(reviewListAdapter);
            reviewListAdapter.setNewData(bb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
