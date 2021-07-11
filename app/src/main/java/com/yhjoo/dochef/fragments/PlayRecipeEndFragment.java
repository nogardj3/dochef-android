package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding;
import com.yhjoo.dochef.model.RecipeDetailPlay;

public class PlayRecipeEndFragment extends Fragment {
    FPlayrecipeItemBinding binding;

    /*
        TODO
        1. 분기 - end용 처리하기
        2. 리뷰작성 기능
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipeDetailPlay recipeDetailPlay = (RecipeDetailPlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeDetailPlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.playrecipeItemImg);

        binding.playrecipeItemExplain.setText(recipeDetailPlay.getExplain());

        binding.playrecipeEndTags.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getTags().length; i++) {
            AppCompatTextView textView = (AppCompatTextView) getLayoutInflater().inflate(R.layout.v_tag,null);
            textView.setText("#" + recipeDetailPlay.getTags()[i] + " ");

            binding.playrecipeEndTags.addView(textView);
        }

        return view;
    }

}
