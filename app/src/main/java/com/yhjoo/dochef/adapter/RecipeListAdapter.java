package com.yhjoo.dochef.adapter;

import android.text.Html;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        helper.setText(R.id.recipemylist_recipetitle, item.getRecipeName());
        helper.setText(R.id.recipemylist_nickname,
                String.format(mContext.getResources().getString(R.string.string_format_usernickname),item.getNickname()));
        helper.setVisible(R.id.recipemylist_mine, item.getNickname().equals("나"));
//            helper.setVisible(R.id.recipemylist_revise, item.getNickName().equals("나") && currentOperation == OPERATION.VIEW);
//            helper.setVisible(R.id.recipemylist_delete, currentOperation == OPERATION.VIEW);
        helper.addOnClickListener(R.id.recipemylist_revise);
        helper.addOnClickListener(R.id.recipemylist_delete);
    }
}