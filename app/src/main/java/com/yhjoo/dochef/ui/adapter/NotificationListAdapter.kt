package com.yhjoo.dochef.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.NotificationItem
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.Utils

class NotificationListAdapter :
    BaseQuickAdapter<NotificationItem, BaseViewHolder>(R.layout.li_notification) {
    override fun convert(helper: BaseViewHolder, item: NotificationItem) {
        ImageLoadUtil.loadUserImage(
            mContext,
            item.img,
            helper.getView<View>(R.id.notification_userimg) as AppCompatImageView
        )
        helper.setText(R.id.notification_contents, item.contents)
        helper.setText(R.id.notification_date, Utils.convertMillisToText(item.dateTime))
        helper.setBackgroundColor(
            R.id.notification_item,
            if (item.isRead == 0) mContext.getColor(R.color.white) else mContext.getColor(R.color.grey)
        )
    }
}