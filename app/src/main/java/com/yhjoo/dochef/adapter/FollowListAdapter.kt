package com.yhjoo.dochef.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.FollowlistItemBinding
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils
import java.util.ArrayList

class FollowListAdapter(
    var activeUserID: String,
    private val subscribeListener: (UserBrief) -> Unit,
    private val unsubscribeListener: (UserBrief) -> Unit,
    private val itemClickListener: (UserBrief) -> Unit
    ) :
    ListAdapter<UserBrief, FollowListAdapter.FollowListViewHolder>(
        FollowListComparator()
    ) {
    lateinit var context: Context
    var activeUserFollowList =  ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        context = parent.context

        val binding = DataBindingUtil.inflate<FollowlistItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.followlist_item,
            parent,
            false
        )

        return FollowListViewHolder(binding)
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

                userFollowBtn.visibility = if (!activeUserFollowList.contains(userBrief.userID) &&userBrief.userID != activeUserID )
                    View.VISIBLE
                else
                    View.GONE
                userFollowcancelBtn.visibility = if (activeUserFollowList.contains(userBrief.userID) && userBrief.userID != activeUserID)
                    View.VISIBLE
                else
                    View.GONE
//                if(activeUserFollowList.contains(userBrief.userID)){
//                    userFollowBtn.visibility = View.GONE
//                    userFollowcancelBtn.visibility = View.VISIBLE
//                }
//                else{
//                    userFollowBtn.visibility = View.VISIBLE
//                    userFollowcancelBtn.visibility = View.GONE
//                }
//                if(userBrief.userID == activeUserID){
//                    userFollowBtn.visibility = View.VISIBLE
//                    userFollowcancelBtn.visibility = View.GONE
//                }


                userFollowBtn.setOnClickListener {
                    subscribeListener(userBrief)
                }
                userFollowcancelBtn.setOnClickListener {
                    unsubscribeListener(userBrief)
                }

                GlideImageLoadDelegator.loadUserImage(context, userBrief.userImg, userImg)
                userNickname.text = userBrief.nickname
                userFollowerCount.text = String.format(
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
