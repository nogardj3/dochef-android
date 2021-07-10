package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.RecipeDetailPlay;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayRecipeItemFragment extends Fragment {
    @BindView(R.id.playrecipe_item_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_item_ingredients)
    FlexboxLayout recipeIngredients;
    @BindView(R.id.playrecipe_item_explain)
    AppCompatTextView recipeExplain;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_playrecipe_item, container, false);
        ButterKnife.bind(this, view);

        RecipeDetailPlay recipeDetailPlay = (RecipeDetailPlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeDetailPlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(recipeImage);

        recipeIngredients.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getIngredients().length; i++) {
            LinearLayout motherview = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.v_ingredient, null);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_product)).setText(recipeDetailPlay.getIngredients()[i]);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_quantity)).setText("0");
            recipeIngredients.addView(motherview);
        }

        recipeExplain.setText(recipeDetailPlay.getExplain());

        return view;
    }
}
