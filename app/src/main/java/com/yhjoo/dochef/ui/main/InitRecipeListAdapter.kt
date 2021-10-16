package com.yhjoo.dochef.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.RecipeHorizontalItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class InitRecipeListAdapter(
    private val layoutType: Int,
    private val itemClickListener: ((Recipe) -> Unit)?
) :
    ListAdapter<Recipe, RecyclerView.ViewHolder>(RecipeListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return when (layoutType) {
            LayoutType.MAIN_INIT -> RecipeHorizontalViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.recipe_horizontal_item,
                    parent,
                    false
                )
            )
            else -> RecipeHorizontalViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.recipe_horizontal_item,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecipeHorizontalViewHolder -> holder.bind(getItem(position))
//            is RecipeHorizontalViewHolder -> holder.bind(getItem(position))
        }
    }

    companion object {
        object LayoutType {
            const val MAIN_INIT = 0
        }
    }

    inner class RecipeHorizontalViewHolder(val binding: RecipeHorizontalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ImageLoaderUtil.loadRecipeImage(
                    context,
                    recipe.recipeImg,
                    recipehorizontalRecipeimg
                )
                recipehorizontalTitle.text = recipe.recipeName
                recipehorizontalRating.text = String.format("%.1f", recipe.rating)
                recipehorizontalView.text = recipe.viewCount.toString()
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