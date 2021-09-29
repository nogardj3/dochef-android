package com.yhjoo.dochef.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.HomeRecipelistItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.ValidateUtil

class RecipeListAdapter(
    private val activeUserID: String?,
    private val itemClickListener: ((Recipe) -> Unit)?
) :
    ListAdapter<Recipe, RecipeListAdapter.HomeRecipeViewHolder>(RecipeListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeListAdapter.HomeRecipeViewHolder {
        context = parent.context

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

                homeRecipeMy.isVisible = recipe.userID == activeUserID
                homeRecipeIsFavorite.isVisible = recipe.userID != activeUserID
                homeRecipeNew.isVisible = ValidateUtil.checkNew(recipe.datetime)
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
