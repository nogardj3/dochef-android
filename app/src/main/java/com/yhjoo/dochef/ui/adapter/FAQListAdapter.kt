package com.yhjoo.dochef.adapter

import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle

class FAQListAdapter(data: List<MultiItemEntity>?) :
    BaseMultiItemQuickAdapter<MultiItemEntity?, BaseViewHolder?>(data) {
    private val EXPAND_DEPTH_0 = 0
    private val EXPAND_CONTENTS = 1
    protected override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (helper.itemViewType) {
            EXPAND_DEPTH_0 -> {
                val lv0 = item as ExpandTitle
                helper.setText(R.id.exp_title_title, lv0.title)
                    .setImageResource(
                        R.id.exp_title_icon,
                        if (lv0.isExpanded) R.drawable.ic_arrow_downward else R.drawable.ic_arrow_right_grey
                    )
                helper.itemView.setOnClickListener { v: View? ->
                    val pos = helper.adapterPosition
                    if (lv0.isExpanded) {
                        collapse(pos)
                    } else {
                        expand(pos)
                    }
                }
            }
            EXPAND_CONTENTS -> {
                val contents = item as ExpandContents
                helper.setText(R.id.exp_contents_contents, contents.text)
            }
        }
    }

    init {
        addItemType(EXPAND_DEPTH_0, R.layout.li_expand_title)
        addItemType(EXPAND_CONTENTS, R.layout.li_expand_contents)
    }
}