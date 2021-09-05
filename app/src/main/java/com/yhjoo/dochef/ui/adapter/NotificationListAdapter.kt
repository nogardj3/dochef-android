package com.yhjoo.dochef.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Notification
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.Utils

class NotificationListAdapter :
    BaseQuickAdapter<Notification, BaseViewHolder>(R.layout.li_notification) {
    override fun convert(helper: BaseViewHolder, item: Notification) {
        ImageLoadUtil.loadUserImage(
            mContext,
            item.image,
            helper.getView<View>(R.id.notification_userimg) as AppCompatImageView
        )
        helper.setText(R.id.notification_contents, item.contents)
        helper.setText(R.id.notification_date, Utils.convertMillisToText(item.dateTime))
        helper.setBackgroundColor(
            R.id.notification_item,
            if (item.read == 0) mContext.getColor(R.color.white) else mContext.getColor(R.color.grey)
        )
    }
}