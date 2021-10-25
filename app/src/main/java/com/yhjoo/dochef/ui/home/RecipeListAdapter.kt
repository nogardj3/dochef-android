package com.yhjoo.dochef.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.HomeRecipelistItemBinding

class RecipeListAdapter(private val containerActivity: HomeActivity) :
    ListAdapter<Recipe, RecipeListAdapter.HomeRecipeViewHolder>(RecipeListComparator()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeListAdapter.HomeRecipeViewHolder {
        return HomeRecipeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.home_recipelist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeListAdapter.HomeRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomeRecipeViewHolder(val binding: HomeRecipelistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                activeUserId = App.activeUserId
                activity = containerActivity
                recipe = item
            }
        }
    }

    class RecipeListComparator : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(
            oldItem: Recipe,
            newItem: Recipe
        ): Boolean {
            return oldItem.recipeID == newItem.recipeID
        }

        override fun areContentsTheSame(
            oldItem: Recipe,
            newItem: Recipe
        ): Boolean {
            return oldItem == newItem
        }
    }
}
