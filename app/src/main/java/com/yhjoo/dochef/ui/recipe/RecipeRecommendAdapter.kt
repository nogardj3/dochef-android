package com.yhjoo.dochef.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.ReciperecommendItemBinding

class RecipeRecommendAdapter(
    private val containerActivity: RecipeRecommendActivity
) :
    ListAdapter<Recipe, RecipeRecommendAdapter.RecipeRecommendListViewHolder>(RecipeRecommendListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeRecommendListViewHolder {
        return RecipeRecommendListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.reciperecommend_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeRecommendListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeRecommendListViewHolder(val binding: ReciperecommendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                activity = containerActivity
                recipe = item
                activeUserId = App.activeUserId
            }
        }
    }

    class RecipeRecommendListComparator : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(
            oldItem: Recipe,
            newItem: Recipe
        ): Boolean {
            return oldItem.userID == newItem.userID
        }

        override fun areContentsTheSame(
            oldItem: Recipe,
            newItem: Recipe
        ): Boolean {
            return oldItem == newItem
        }
    }
}