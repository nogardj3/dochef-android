package com.yhjoo.dochef.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.entity.NotificationEntity
import com.yhjoo.dochef.databinding.NotificationItemBinding

class NotificationListAdapter(private val containerViewModel: NotificationViewModel) :
    ListAdapter<NotificationEntity, NotificationListAdapter.NoticeViewHolder>(NotificationComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
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
        fun bind(item: NotificationEntity) {
            binding.apply {
                viewModel = containerViewModel
                notification = item
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
