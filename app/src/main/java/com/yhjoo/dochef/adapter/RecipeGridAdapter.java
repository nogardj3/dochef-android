package com.yhjoo.dochef.adapter;

import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.RecipeBrief;
import com.yhjoo.dochef.utils.Utils;

public class RecipeGridAdapter extends BaseQuickAdapter<RecipeBrief, BaseViewHolder> {
    RecyclerView recyclerView;

    public RecipeGridAdapter(RecyclerView recyclerView) {
        super(R.layout.li_recipe_home);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecipeBrief item) {
        ViewGroup.LayoutParams lp = helper.itemView.findViewById(R.id.recipehome_recipeimg).getLayoutParams();

        lp.width = recyclerView.getMeasuredWidth() / 3;
        lp.height = recyclerView.getMeasuredWidth() / 3;
        helper.itemView.findViewById(R.id.recipehome_recipeimg).setLayoutParams(lp);

        Glide.with(mContext)
                .load(Integer.valueOf(item.getRecipeImg()))
                .into((AppCompatImageView) helper.getView(R.id.recipehome_recipeimg));

        helper.setVisible(R.id.recipehome_new, Utils.checkNew(item.getDatetime()));
    }
}