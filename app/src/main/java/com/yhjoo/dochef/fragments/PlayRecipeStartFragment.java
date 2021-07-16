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
import com.yhjoo.dochef.model.RecipePlay;

public class PlayRecipeStartFragment extends Fragment {
    FPlayrecipeStartBinding binding;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeStartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipePlay recipePlay = (RecipePlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipePlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.playrecipeStartImg);

        binding.playrecipeStartTitle.setText(recipePlay.getTitle());
        binding.playrecipeStartExplain.setText(recipePlay.getExplain());

        binding.playrecipeStartIngredients.removeAllViews();
        for (int i = 0; i < recipePlay.getIngredients().length; i++) {
            ConstraintLayout motherview = (ConstraintLayout) getLayoutInflater().inflate(R.layout.li_ingredient, null);
            AppCompatTextView view1 = (motherview.findViewById(R.id.ingredient_product));
            view1.setTextColor(getResources().getColor(R.color.white,null));
            view1.setText(recipePlay.getIngredients()[i]);
            AppCompatTextView view2 = (motherview.findViewById(R.id.ingredient_quantity));
            view2.setTextColor(getResources().getColor(R.color.white,null));
            view2.setText(recipePlay.getIngredients()[i]);

            binding.playrecipeStartIngredients.addView(motherview);
        }

        return view;
    }
}
