package com.yhjoo.dochef.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.classes.RecipeItem;

public class PlayRecipeStartFragment extends Fragment {
    @BindView(R.id.playrecipe_start_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_start_title)
    AppCompatTextView recipeTitle;
    @BindView(R.id.playrecipe_start_explain)
    AppCompatTextView recipeExplain;
    @BindView(R.id.playrecipe_start_ingredients)
    FlexboxLayout recipeIngredients;

    private RecipeItem recipeItem;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_playrecipe_start, container, false);
        ButterKnife.bind(this, view);

        recipeItem = (RecipeItem) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeItem.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(recipeImage);

        recipeTitle.setText(recipeItem.getTitle());
        recipeExplain.setText(recipeItem.getExplain());

        recipeIngredients.removeAllViews();
        for (int i = 0; i < recipeItem.getIngredients().length; i++) {
            @SuppressLint("InflateParams") PercentRelativeLayout motherview = (PercentRelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.li_ingredient, null);
            AppCompatTextView view1 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_product));
            view1.setTextColor(getResources().getColor(R.color.white));
            view1.setText(recipeItem.getIngredients()[i]);
            AppCompatTextView view2 = ((AppCompatTextView) motherview.findViewById(R.id.li_ingredient_quantity));
            view2.setTextColor(getResources().getColor(R.color.white));
            view2.setText(recipeItem.getIngredients()[i]);

            recipeIngredients.addView(motherview);
        }

        return view;
    }
}
