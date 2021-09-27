package com.yhjoo.dochef.ui.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil
import com.yhjoo.dochef.utils.ValidateUtil

class RecipeListHorizontalAdapter(
    private val layoutType: Int,
    private val activeUserID: String?,
    private val itemClickListener: ((Recipe) -> Unit)?
) :
    ListAdapter<Recipe, RecyclerView.ViewHolder>(RecipeListComparator()) {
    companion object {
        const val MAIN_INIT = 0
        const val HOME = 1
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return when (layoutType) {
            MAIN_INIT -> RecipeHorizontalViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.recipe_horizontal_item,
                    parent,
                    false
                )
            )
            else -> HomeRecipeViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.recipetheme_item,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecipeHorizontalViewHolder -> holder.bind(getItem(position))
            is HomeRecipeViewHolder -> holder.bind(getItem(position))
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

    inner class HomeRecipeViewHolder(val binding: HomeRecipelistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                binding.root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ImageLoaderUtil.loadRecipeImage(
                    context,
                    recipe.recipeImg,
                    homeRecipeRecipeimg
                )

                homeRecipeName.text = recipe.recipeName

                homeRecipeMy.visibility = if (activeUserID == recipe.userID)
                    View.VISIBLE
                else
                    View.GONE
                homeRecipeIsFavorite.visibility = if (activeUserID != recipe.userID)
                    View.VISIBLE
                else
                    View.GONE

                homeRecipeNew.visibility = if (ValidateUtil.checkNew(recipe.datetime))
                    View.VISIBLE
                else
                    View.GONE
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
