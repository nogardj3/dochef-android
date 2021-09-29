package com.yhjoo.dochef.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.databinding.CommentItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil

class CommentListAdapter(
    private val userId: String,
    private val otherClickListener: (View,Comment) -> Unit
) :
    ListAdapter<Comment, CommentListAdapter.CommentListViewHolder>(CommentListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        context = parent.context

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
        fun bind(comment: Comment) {
            binding.apply {
                ImageLoaderUtil.loadUserImage(
                    context,
                    comment.userImg,
                    commentUserimg
                )

                commentNickname.text = comment.nickName
                commentContents.text = comment.contents
                commentDate.text = OtherUtil.millisToText(comment.dateTime)
                commentOther.isVisible = comment.userID == userId
                commentOther.setOnClickListener {
                    otherClickListener(it,comment)
                }
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