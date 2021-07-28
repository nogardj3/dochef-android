package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.Utils;

public class PlayRecipeItemFragment extends Fragment {
    FPlayrecipeItemBinding binding;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipePhase recipePhase = (RecipePhase) getArguments().getSerializable("item");

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

        return view;
    }
}
