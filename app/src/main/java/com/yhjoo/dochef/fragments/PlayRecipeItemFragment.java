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
import com.yhjoo.dochef.model.RecipeDetailPlay;

public class PlayRecipeItemFragment extends Fragment {
    FPlayrecipeItemBinding binding;

    /*
        TODO
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

        binding.playrecipeItemIngredients.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getIngredients().length; i++) {
            LinearLayout motherview = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.v_ingredient, null);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_product)).setText(recipeDetailPlay.getIngredients()[i]);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_quantity)).setText("0");
            binding.playrecipeItemIngredients.addView(motherview);
        }

        binding.playrecipeItemExplain.setText(recipeDetailPlay.getExplain());

        return view;
    }
}
