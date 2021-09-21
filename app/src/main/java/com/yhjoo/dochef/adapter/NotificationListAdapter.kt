package com.yhjoo.dochef.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.NotificationItemBinding
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class NotificationListAdapter(private val clickListener: (NotificationEntity) -> Unit) :
    ListAdapter<NotificationEntity, NotificationListAdapter.NoticeViewHolder>(NotificationComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        context = parent.context

        val binding = DataBindingUtil.inflate<NotificationItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.notification_item,
            parent,
            false
        )

        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoticeViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationEntity) {
            binding.apply {
                root.setBackgroundColor(
                    if (notification.isRead == 0) context.getColor(R.color.white)
                    else context.getColor(R.color.grey)
                )
                root.setOnClickListener {
                    clickListener(notification)
                }

                GlideImageLoadDelegator.loadUserImage(
                    context,
                    notification.img,
                    notificationUserimg
                )

                notificationContents.text = notification.contents
                notificationDate.text = Utils.convertMillisToText(notification.dateTime)
            }
        }
    }

    class NotificationComparator : DiffUtil.ItemCallback<NotificationEntity>() {
        override fun areItemsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
