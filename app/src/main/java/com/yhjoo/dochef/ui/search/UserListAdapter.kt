package com.yhjoo.dochef.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.SearchResultUserItemBinding

class UserListAdapter(
    private val containerFragment: ResultUserFragment,
) :
    ListAdapter<UserBrief, UserListAdapter.ResultUserViewHolder>(ResultUserComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultUserViewHolder {
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
        fun bind(item: UserBrief) {
            binding.apply {
                fragment = containerFragment
                userBrief = item
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