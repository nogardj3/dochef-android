package com.yhjoo.dochef.ui.setting

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.ExpandContents
import com.yhjoo.dochef.data.model.ExpandTitle
import com.yhjoo.dochef.utils.OtherUtil

class ExpandableNoticeListAdapter(data: List<MultiItemEntity>?) :
    BaseMultiItemQuickAdapter<MultiItemEntity?, BaseViewHolder?>(data) {
    object EXPAND {
        const val DEPTH_0 = 0
        const val CONTENTS = 1
    }

    init {
        addItemType(EXPAND.DEPTH_0, R.layout.expand_item_title)
        addItemType(EXPAND.CONTENTS, R.layout.expand_item_contents)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        if (helper != null) {
            when (helper.itemViewType) {
                EXPAND.DEPTH_0 -> {
                    val lv0 = item as ExpandTitle
                    helper.setText(R.id.exp_title_title, lv0.title)
                        .setImageResource(
                            R.id.exp_title_icon,
                            if (lv0.isExpanded) R.drawable.ic_arrow_downward else R.drawable.ic_arrow_right_grey
                        )
                    helper.itemView.setOnClickListener {
                        val pos = helper.adapterPosition
                        if (lv0.isExpanded) {
                            collapse(pos)
                        } else {
                            expand(pos)
                        }
                    }
                }
                EXPAND.CONTENTS -> {
                    val contents = item as ExpandContents
                    helper.setText(R.id.exp_contents_contents, contents.text)
                    helper.setText(
                        R.id.exp_contents_date,
                        OtherUtil.millisToText(contents.date)
                    )
                }
            }
        }
    }
}