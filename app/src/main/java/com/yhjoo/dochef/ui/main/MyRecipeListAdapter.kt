package com.yhjoo.dochef.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.MainMyrecipeItemBinding

class MyRecipeListAdapter(private val containerFragment: MyRecipeFragment) :
    ListAdapter<Recipe, MyRecipeListAdapter.MyRecipesViewHolder>(RecipeListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipesViewHolder {
        return MyRecipesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.main_myrecipe_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyRecipesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyRecipesViewHolder(val binding: MainMyrecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.apply {
                activeUserId = App.activeUserId
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
