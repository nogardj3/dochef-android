package com.yhjoo.dochef.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.MainInitItemBinding

class InitRecipeListAdapter(private val containerFragment: InitFragment) :
    ListAdapter<Recipe, InitRecipeListAdapter.InitRecipeViewHolder>(RecipeListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InitRecipeViewHolder {
        return InitRecipeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.main_init_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InitRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InitRecipeViewHolder(val binding: MainInitItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                fragment = containerFragment
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
