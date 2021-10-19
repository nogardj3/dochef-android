package com.yhjoo.dochef.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.databinding.ReviewItemBinding

class ReviewListAdapter(
    private val containerActivity: RecipeDetailActivity
) :
    ListAdapter<Review, ReviewListAdapter.ReviewListViewHolder>(ReviewListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
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
        fun bind(item: Review) {
            binding.apply {
                activity = containerActivity
                review = item
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