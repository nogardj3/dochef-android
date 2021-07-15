package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding;
import com.yhjoo.dochef.model.RecipePlay;

public class PlayRecipeItemFragment extends Fragment {
    FPlayrecipeItemBinding binding;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipePlay recipePlay = (RecipePlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipePlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.playrecipeItemImg);

        binding.playrecipeItemIngredients.removeAllViews();
        for (int i = 0; i < recipePlay.getIngredients().length; i++) {
            LinearLayout motherview = (LinearLayout) getLayoutInflater().inflate(R.layout.v_ingredient, null);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_name)).setText(recipePlay.getIngredients()[i]);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_amount)).setText("0");
            binding.playrecipeItemIngredients.addView(motherview);
        }

        binding.playrecipeItemExplain.setText(recipePlay.getExplain());

        return view;
    }
}
