package com.yhjoo.dochef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.WriteReviewActivity;
import com.yhjoo.dochef.classes.RecipeItem;

public class PlayRecipeEndFragment extends Fragment {
    @BindView(R.id.playrecipe_end_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_end_explain)
    AppCompatTextView recipeExplain;
    @BindView(R.id.playrecipe_end_tags)
    FlexboxLayout recipeTags;

    private RecipeItem recipeItem;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_playrecipe_end, container, false);
        ButterKnife.bind(this, view);

        recipeItem = (RecipeItem) getArguments().getSerializable("item");

        Glide.with(getContext())
                .load(recipeItem.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(recipeImage);

        recipeExplain.setText(recipeItem.getExplain());

        recipeTags.removeAllViews();
        for (int i = 0; i < recipeItem.getTags().length; i++) {
            AppCompatTextView textView = new AppCompatTextView(getContext());
            textView.setText("#" + recipeItem.getTags()[i] + " ");
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));

            recipeTags.addView(textView);
        }

        return view;
    }

    @OnClick({R.id.playrecipe_end_review})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.playrecipe_end_review:
                startActivity(new Intent(PlayRecipeEndFragment.this.getContext(), WriteReviewActivity.class));
                break;
        }
    }
}
