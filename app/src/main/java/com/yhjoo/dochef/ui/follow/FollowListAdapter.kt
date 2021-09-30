package com.yhjoo.dochef.ui.follow

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.FollowlistItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil
import java.util.*

class FollowListAdapter(
    private val activeUserID: String,
    private val subscribeListener: (UserBrief) -> Unit,
    private val unsubscribeListener: (UserBrief) -> Unit,
    private val itemClickListener: (UserBrief) -> Unit
) :
    ListAdapter<UserBrief, FollowListAdapter.FollowListViewHolder>(FollowListComparator()) {
    lateinit var context: Context
    var activeUserFollowList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        context = parent.context

        return FollowListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
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
        fun bind(userBrief: UserBrief) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(userBrief)
                }

                followlistFollowBtn.isVisible =
                    !activeUserFollowList.contains(userBrief.userID) && userBrief.userID != activeUserID
                followlistFollowcancelBtn.isVisible =
                    activeUserFollowList.contains(userBrief.userID) && userBrief.userID != activeUserID

                followlistFollowBtn.setOnClickListener {
                    subscribeListener(userBrief)
                }
                followlistFollowcancelBtn.setOnClickListener {
                    unsubscribeListener(userBrief)
                }

                ImageLoaderUtil.loadUserImage(context, userBrief.userImg, followlistImg)
                followlistNickname.text = userBrief.nickname
                followlistFollowerCount.text = String.format(
                    context.getString(R.string.format_follower), userBrief.follower_count
                )
            }
        }
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
