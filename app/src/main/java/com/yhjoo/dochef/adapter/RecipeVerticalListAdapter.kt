package com.yhjoo.dochef.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.ui.activities.HomeActivity.OPERATION.VIEW
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class RecipeVerticalListAdapter(
    private val activeUserID: String,
    private val layoutType: Int,
    private val itemClickListener: (Recipe) -> Unit
) :
    ListAdapter<Recipe, RecyclerView.ViewHolder>(RecipeListComparator()) {

    companion object {
        object LAYOUTTYPE {
            const val MAIN_RECIPES = 0
            const val MAIN_MYRECIPE = 1
            const val RECIPELIST = 2
            const val THEME = 3
        }
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return when (layoutType) {
            LAYOUTTYPE.MAIN_RECIPES -> MainRecipesViewHolder(
                DataBindingUtil.inflate<MainRecipesItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.main_recipes_item,
                    parent,
                    false
                )
            )
            LAYOUTTYPE.MAIN_MYRECIPE -> MainMyRecipeViewHolder(
                DataBindingUtil.inflate<MainMyrecipesItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.main_recipes_item,
                    parent,
                    false
                )
            )
            LAYOUTTYPE.RECIPELIST -> RecipeListViewHolder(
                DataBindingUtil.inflate<RecipelistItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.main_recipes_item,
                    parent,
                    false
                )
            )
            else -> RecipeThemeViewHolder(
                DataBindingUtil.inflate<RecipethemeItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.main_recipes_item,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainRecipesViewHolder -> holder.bind(getItem(position))
            is MainMyRecipeViewHolder -> holder.bind(getItem(position))
            is RecipeListViewHolder -> holder.bind(getItem(position))
            is RecipeThemeViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class MainRecipesViewHolder(val binding: MainRecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                binding.root.setOnClickListener {
                    itemClickListener(recipe)
                }

                GlideImageLoadDelegator.loadRecipeImage(
                    context,
                    recipe.recipeImg,
                    mainRecipesRecipeImg
                )
                mainRecipesTitle.text = recipe.recipeName
                mainRecipesNickname.text =
                    String.format(
                        context.resources.getString(R.string.format_usernickname),
                        recipe.nickname
                    )
                mainRecipesDate.text = Utils.convertMillisToText(recipe.datetime)
                mainRecipesRating.text = String.format("%.1f", recipe.rating)
                mainRecipesView.text = recipe.viewCount.toString()

                mainRecipesNew.visibility = if (Utils.checkNew(recipe.datetime))
                    View.VISIBLE
                else
                    View.GONE

                mainRecipesYours.visibility = if (activeUserID == recipe.userID)
                    View.VISIBLE
                else
                    View.GONE
            }
        }
    }

    inner class MainMyRecipeViewHolder(val binding: MainMyrecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
        }
    }

    inner class RecipeListViewHolder(val binding: RecipelistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
        }
    }

    inner class RecipeThemeViewHolder(val binding: RecipethemeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
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
