package com.yhjoo.dochef.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.LiNotificationBinding
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class NotificationListAdapter2(private val items: ArrayList<NotificationEntity>) :
    RecyclerView.Adapter<NotificationListAdapter2.NoticeViewHolder>() {
    private lateinit var ctx: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        ctx = parent.context

        val binding = LiNotificationBinding.inflate(
            LayoutInflater.from(ctx), parent, false
        )

        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        Utils.log(item.toString())

        GlideImageLoadDelegator.loadUserImage(
            ctx,
            item.img,
            binding.notificationUserimg
        )
        binding.notificationContents.text = item.contents
        binding.notificationDate.text = Utils.convertMillisToText(item.dateTime)
        binding.notificationItem.setBackgroundColor(
            if (item.isRead == 0) ctx.getColor(R.color.white)
            else ctx.getColor(R.color.grey)
        )
    }

    override fun getItemCount(): Int = items.size

    class NoticeViewHolder(val binding: LiNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)
}


//:
//    BaseQuickAdapter<NotificationEntity, BaseViewHolder>(R.layout.li_notification) {
//    override fun convert(helper: BaseViewHolder, entity: NotificationEntity) {
//        GlideImageLoadDelegator.loadUserImage(
//            mContext,
//            entity.img,
//            helper.getView<View>(R.id.notification_userimg) as AppCompatImageView
//        )
//        helper.setText(R.id.notification_contents, entity.contents)
//        helper.setText(R.id.notification_date, Utils.convertMillisToText(entity.dateTime))
//        helper.setBackgroundColor(
//            R.id.notification_item,
//            if (entity.isRead == 0) mContext.getColor(R.color.white) else mContext.getColor(R.color.grey)
//        )
//    }
//}