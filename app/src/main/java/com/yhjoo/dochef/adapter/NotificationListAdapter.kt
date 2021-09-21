package com.yhjoo.dochef.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class NotificationListAdapter :
    BaseQuickAdapter<NotificationEntity, BaseViewHolder>(R.layout.notification_item) {
    override fun convert(helper: BaseViewHolder, entity: NotificationEntity) {
        GlideImageLoadDelegator.loadUserImage(
            mContext,
            entity.img,
            helper.getView<View>(R.id.notification_userimg) as AppCompatImageView
        )
        helper.setText(R.id.notification_contents, entity.contents)
        helper.setText(R.id.notification_date, Utils.convertMillisToText(entity.dateTime))
        helper.setBackgroundColor(
            R.id.notification_item,
            if (entity.isRead == 0) mContext.getColor(R.color.white) else mContext.getColor(R.color.grey)
        )
    }
}