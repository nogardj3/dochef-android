package com.yhjoo.dochef.adapter;

import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.RecipeThumbnail;

public class RecipeGridAdapter extends BaseQuickAdapter<RecipeThumbnail, BaseViewHolder> {
    RecyclerView recyclerView;

    public RecipeGridAdapter(RecyclerView recyclerView) {
        super(R.layout.li_homegrid);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecipeThumbnail item) {
        ViewGroup.LayoutParams lp = helper.itemView.findViewById(R.id.li_homegrid_recipeimg).getLayoutParams();

        lp.width = recyclerView.getMeasuredWidth() / 3;
        lp.height = recyclerView.getMeasuredWidth() / 3;
        helper.itemView.findViewById(R.id.li_homegrid_recipeimg).setLayoutParams(lp);


        Glide.with(mContext)
                .load(Integer.valueOf(item.getImageUrl()))
                .into((AppCompatImageView) helper.getView(R.id.li_homegrid_recipeimg));

        helper.setVisible(R.id.li_homegrid_type,
                item.getThumbnail_type() == mContext.getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE));
        helper.setVisible(R.id.li_homegrid_new, item.getIsNew() == 1);
    }
}