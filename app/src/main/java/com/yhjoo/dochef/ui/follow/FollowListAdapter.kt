package com.yhjoo.dochef.ui.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.FollowlistItemBinding
import com.yhjoo.dochef.ui.setting.SettingViewModel
import java.util.*

class FollowListAdapter(
    val containerActivity: FollowListActivity,
    val vm: FollowListViewModel
) :
    ListAdapter<UserBrief, FollowListAdapter.FollowListViewHolder>(FollowListComparator()) {
    var activeUserFollowList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        return FollowListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.followlist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FollowListViewHolder(val binding: FollowlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserBrief) {
            binding.apply {
                adapter = this@FollowListAdapter
                activity = containerActivity
                viewModel = vm
                userBrief = item
            }
        }
    }

    fun followBtnVisible(userId: String): Int {
        return if (!activeUserFollowList.contains(userId)
            && userId != App.activeUserId
        )
            View.VISIBLE
        else
            View.GONE
    }

    fun followCancelBtnVisible(userId: String): Int {
        return if (activeUserFollowList.contains(userId)
            && userId != App.activeUserId
        )
            View.VISIBLE
        else
            View.GONE
    }

    class FollowListComparator : DiffUtil.ItemCallback<UserBrief>() {
        override fun areItemsTheSame(
            oldItem: UserBrief,
            newItem: UserBrief
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: UserBrief,
            newItem: UserBrief
        ): Boolean {
            return oldItem == newItem
        }
    }
}
