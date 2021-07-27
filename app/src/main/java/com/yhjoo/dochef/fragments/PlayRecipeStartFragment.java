package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.FPlayrecipeStartBinding;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.model.RecipePlay;
import com.yhjoo.dochef.utils.GlideApp;
import com.yhjoo.dochef.utils.ImageLoadUtil;

public class PlayRecipeStartFragment extends Fragment {
    FPlayrecipeStartBinding binding;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FPlayrecipeStartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecipeDetail recipeDetail = (RecipeDetail) getArguments().getSerializable("item");

        ImageLoadUtil.loadRecipeImage(getContext(),recipeDetail.getRecipeImg(),binding.playrecipeStartImg);

        binding.playrecipeStartTitle.setText(recipeDetail.getRecipeName());
        binding.playrecipeStartContents.setText(recipeDetail.getContents());

        binding.playrecipeStartTags.removeAllViews();
        for (String tag : recipeDetail.getTags()) {
            LinearLayout tagcontainer = (LinearLayout) getLayoutInflater().inflate(R.layout.v_tag_recipe, null);
            AppCompatTextView tagview = tagcontainer.findViewById(R.id.vtag_recipe_text);
            tagview.setText("#" + tag);
            binding.playrecipeStartTags.addView(tagcontainer);
        }

        binding.playrecipeStartIngredients.removeAllViews();
        for (Ingredient ingredient : recipeDetail.getIngredients()) {
            ConstraintLayout ingredientContainer = (ConstraintLayout) getLayoutInflater().inflate(R.layout.v_ingredient_play, null);
            AppCompatTextView ingredientName = ingredientContainer.findViewById(R.id.v_ingredientplay_name);
            ingredientName.setText(ingredient.getName());
            AppCompatTextView ingredientAmount = ingredientContainer.findViewById(R.id.v_ingredientplay_amount);
            ingredientAmount.setText(ingredient.getAmount());
            binding.playrecipeStartIngredients.addView(ingredientContainer);
        }

        return view;
    }
}
