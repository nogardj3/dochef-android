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
import com.skyhope.materialtagview.TagView;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.ReviewWriteActivity;
import com.yhjoo.dochef.model.RecipeDetailPlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayRecipeEndFragment extends Fragment {
    @BindView(R.id.playrecipe_item_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_item_explain)
    AppCompatTextView recipeExplain;
    @BindView(R.id.playrecipe_end_tags)
    TagView recipeTags;

    /*
        TODO
        1. 분기 - end용 처리하기
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

        recipeExplain.setText(recipeDetailPlay.getExplain());

        recipeTags.removeAllViews();
        for (int i = 0; i < recipeDetailPlay.getTags().length; i++) {
            AppCompatTextView textView = (AppCompatTextView) getLayoutInflater().inflate(R.layout.v_tag,null);
            textView.setText("#" + recipeDetailPlay.getTags()[i] + " ");

            recipeTags.addView(textView);
        }

        return view;
    }

    @OnClick({R.id.playrecipe_end_review})
    void oc(View v) {
        if (v.getId() == R.id.playrecipe_end_review)
            startActivity(new Intent(PlayRecipeEndFragment.this.getContext(), ReviewWriteActivity.class));
    }
}
