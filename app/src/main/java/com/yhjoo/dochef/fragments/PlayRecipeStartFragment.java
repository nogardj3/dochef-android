package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.FPlayrecipeStartBinding;
import com.yhjoo.dochef.model.RecipeDetailPlay;

public class PlayRecipeStartFragment extends Fragment {
    FPlayrecipeStartBinding binding;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeStartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipeDetailPlay recipeDetailPlay = (RecipeDetailPlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeDetailPlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.playrecipeStartImg);

        binding.playrecipeStartTitle.setText(recipeDetailPlay.getTitle());
        binding.playrecipeStartExplain.setText(recipeDetailPlay.getExplain());

        binding.playrecipeStartIngredients.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getIngredients().length; i++) {
            ConstraintLayout motherview = (ConstraintLayout) getActivity().getLayoutInflater().inflate(R.layout.li_ingredient, null);
            AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.ingredient_product));
            view1.setTextColor(getResources().getColor(R.color.white,null));
            view1.setText(recipeDetailPlay.getIngredients()[i]);
            AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.ingredient_quantity));
            view2.setTextColor(getResources().getColor(R.color.white,null));
            view2.setText(recipeDetailPlay.getIngredients()[i]);

            binding.playrecipeStartIngredients.addView(motherview);
        }

        return view;
    }
}
