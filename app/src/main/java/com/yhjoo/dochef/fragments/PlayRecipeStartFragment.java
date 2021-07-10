package com.yhjoo.dochef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyhope.materialtagview.TagView;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.RecipeDetailPlay;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayRecipeStartFragment extends Fragment {
    @BindView(R.id.playrecipe_start_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_start_title)
    AppCompatTextView recipeTitle;
    @BindView(R.id.playrecipe_start_explain)
    AppCompatTextView recipeExplain;
    @BindView(R.id.playrecipe_start_ingredients)
    TagView recipeIngredients;

    /*
        TODO
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_playrecipe_start, container, false);
        ButterKnife.bind(this, view);

        RecipeDetailPlay recipeDetailPlay = (RecipeDetailPlay) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeDetailPlay.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(recipeImage);

        recipeTitle.setText(recipeDetailPlay.getTitle());
        recipeExplain.setText(recipeDetailPlay.getExplain());

        recipeIngredients.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getIngredients().length; i++) {
            ConstraintLayout motherview = (ConstraintLayout) getActivity().getLayoutInflater().inflate(R.layout.li_ingredient, null);
            AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_product));
            view1.setTextColor(getResources().getColor(R.color.white,null));
            view1.setText(recipeDetailPlay.getIngredients()[i]);
            AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_quantity));
            view2.setTextColor(getResources().getColor(R.color.white,null));
            view2.setText(recipeDetailPlay.getIngredients()[i]);

            recipeIngredients.addView(motherview);
        }

        return view;
    }
}
