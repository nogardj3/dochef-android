package com.yhjoo.dochef.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.SearchResultUserItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class ResultUserAdapter(
    private val itemClickListener: (UserBrief) -> Unit
) :
    ListAdapter<UserBrief, ResultUserAdapter.ResultUserViewHolder>(ResultUserComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultUserViewHolder {
        context = parent.context

        return ResultUserViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.search_result_user_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ResultUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultUserViewHolder(val binding: SearchResultUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userBrief: UserBrief) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(userBrief)
                }
                ImageLoaderUtil.loadUserImage(
                    context,
                    userBrief.userImg,
                    resultuserImg
                )
                resultuserNickname.text = userBrief.nickname
                resultuserFollowerCount.text = String.format(
                    context.getString(R.string.format_follower),
                    userBrief.follower_count
                )
            }
        }
    }

    class ResultUserComparator : DiffUtil.ItemCallback<UserBrief>() {
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