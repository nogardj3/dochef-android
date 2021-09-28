package com.yhjoo.dochef.ui.recipe

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.databinding.ReviewItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil

class ReviewListAdapter(
    private val userClickListener: (Review) -> Unit
) :
    ListAdapter<Review, ReviewListAdapter.ReviewListViewHolder>(ReviewListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        context = parent.context

        return ReviewListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.review_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewListViewHolder(val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.apply {
                reviewUserWrapper.setOnClickListener {
                    userClickListener(review)
                }
                ImageLoaderUtil.loadUserImage(
                    context,
                    review.userImg,
                    reviewUserimg
                )
                reviewNickname.text = review.nickname
                reviewContents.text = review.contents
                reviewDatetext.text = OtherUtil.millisToText(review.dateTime)
                reviewRating.rating = review.rating.toFloat()
            }
        }
    }

    class ReviewListComparator : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(
            oldItem: Review,
            newItem: Review
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: Review,
            newItem: Review
        ): Boolean {
            return oldItem == newItem
        }
    }
}