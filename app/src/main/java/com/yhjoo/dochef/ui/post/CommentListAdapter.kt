package com.yhjoo.dochef.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.databinding.CommentItemBinding

class CommentListAdapter(
    private val containerActivity: PostDetailActivity
) :
    ListAdapter<Comment, CommentListAdapter.CommentListViewHolder>(CommentListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        return CommentListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.comment_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentListViewHolder(val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) {
            binding.apply {
                comment = item
                activity = containerActivity
                activeUserId = App.activeUserId
            }
        }
    }

    class CommentListComparator : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem.commentID == newItem.commentID
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem == newItem
        }
    }
}