package com.yhjoo.dochef.adapter;

import android.text.Html;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Recipe;

import java.util.ArrayList;

public class RecipeListAdapter extends BaseItemDraggableAdapter<Recipe, BaseViewHolder> {
    public RecipeListAdapter() {
        super(R.layout.li_recipe_mylist, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Recipe item) {
        Glide.with(mContext)
                .load(item.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into((AppCompatImageView) helper.getView(R.id.recipemylist_recipeimg));

        helper.setText(R.id.recipemylist_recipetitle, item.getTitle());
        helper.setText(R.id.recipemylist_nickname, Html.fromHtml("By - <b>" + item.getNickName() + "</b>", Html.FROM_HTML_MODE_LEGACY));
        helper.setVisible(R.id.recipemylist_mine, item.getNickName().equals("나"));
//            helper.setVisible(R.id.recipemylist_revise, item.getNickName().equals("나") && currentOperation == OPERATION.VIEW);
//            helper.setVisible(R.id.recipemylist_delete, currentOperation == OPERATION.VIEW);
//            helper.setVisible(R.id.recipemylist_touch, currentOperation == OPERATION.ALIGN);
        helper.addOnClickListener(R.id.recipemylist_revise);
        helper.addOnClickListener(R.id.recipemylist_delete);
    }
}