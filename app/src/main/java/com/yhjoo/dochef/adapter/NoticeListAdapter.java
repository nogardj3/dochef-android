package com.yhjoo.dochef.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.ExpandContents;
import com.yhjoo.dochef.model.ExpandTitle;
import com.yhjoo.dochef.utils.Utils;

import java.util.List;

public class NoticeListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private final int EXPAND_DEPTH_0 = 0;
    private final int EXPAND_CONTENTS = 1;

    public NoticeListAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(EXPAND_DEPTH_0, R.layout.li_expand_title);
        addItemType(EXPAND_CONTENTS, R.layout.li_expand_contents);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case EXPAND_DEPTH_0:
                final ExpandTitle lv0 = (ExpandTitle) item;
                helper.setText(R.id.exp_title_title, lv0.title)
                        .setImageResource(R.id.exp_title_icon, lv0.isExpanded() ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow);
                helper.itemView.setOnClickListener(v -> {
                    int pos = helper.getAdapterPosition();
                    if (lv0.isExpanded()) {
                        collapse(pos);
                    } else {
                        expand(pos);
                    }
                });
                break;

            case EXPAND_CONTENTS:
                final ExpandContents contents = (ExpandContents) item;
                helper.setText(R.id.exp_contents_contents, contents.getText());
                helper.setText(R.id.exp_contents_date, Utils.convertMillisToText(contents.getDate()));

                break;
        }
    }
}