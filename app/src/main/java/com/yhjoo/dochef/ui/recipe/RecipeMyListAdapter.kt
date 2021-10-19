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
import com.yhjoo.dochef.databinding.RecipemylistItemBinding

class RecipeMyListAdapter(
    private val containerActivity: RecipeMyListActivity
) :
    ListAdapter<Recipe, RecipeMyListAdapter.RecipeMyListViewHolder>(RecipeMyListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeMyListViewHolder {
        return RecipeMyListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipemylist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeMyListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeMyListViewHolder(val binding: RecipemylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                activity = containerActivity
                recipe = item
                activeUserId = App.activeUserId
            }
        }
    }

    class RecipeMyListComparator : DiffUtil.ItemCallback<Recipe>() {
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