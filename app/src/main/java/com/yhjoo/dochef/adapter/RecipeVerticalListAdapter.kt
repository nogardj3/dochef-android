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
import com.yhjoo.dochef.databinding.MainMyrecipeItemBinding
import com.yhjoo.dochef.databinding.MainRecipesItemBinding
import com.yhjoo.dochef.databinding.RecipemylistItemBinding
import com.yhjoo.dochef.databinding.RecipethemeItemBinding
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.utilities.ChefImageLoader
import com.yhjoo.dochef.utilities.Utils

class RecipeVerticalListAdapter(
    private val layoutType: Int,
    private val activeUserID: String?,
    private val itemClickListener: ((Recipe) -> Unit)?
) :
    ListAdapter<Recipe, RecyclerView.ViewHolder>(RecipeListComparator()) {
    companion object {
        const val MAIN_RECIPES = 0
        const val MAIN_MYRECIPE = 1
        const val SEARCH_RECIPE = 2
        const val SEARCH_INGREDIENT = 3
        const val SEARCH_TAG = 4
        const val MYLIST = 5
        const val THEME = 6
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return when (layoutType) {
            MAIN_RECIPES -> MainRecipesViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.main_recipes_item,
                    parent,
                    false
                )
            )
            MAIN_MYRECIPE -> MainMyRecipeViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.main_myrecipe_item,
                    parent,
                    false
                )
            )
            MYLIST -> RecipeMyListViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.recipemylist_item,
                    parent,
                    false
                )
            )
            else -> RecipeThemeViewHolder(
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
        Utils.log("bindbind~~~~", holder.toString())
        when (holder) {
            is MainRecipesViewHolder -> holder.bind(getItem(position))
            is MainMyRecipeViewHolder -> holder.bind(getItem(position))
            is RecipeMyListViewHolder -> holder.bind(getItem(position))
            is RecipeThemeViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class MainRecipesViewHolder(val binding: MainRecipesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ChefImageLoader.loadRecipeImage(
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

    inner class MainMyRecipeViewHolder(val binding: MainMyrecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                binding.root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ChefImageLoader.loadRecipeImage(
                    context,
                    recipe.recipeImg,
                    mainmyrecipeRecipeimg
                )
                mainmyrecipeTitle.text = recipe.recipeName
                mainmyrecipeNickname.text =
                    String.format(
                        context.resources.getString(R.string.format_usernickname),
                        recipe.nickname
                    )
                mainmyrecipeDate.text = Utils.convertMillisToText(recipe.datetime)
                mainmyrecipeRating.text = String.format("%.1f", recipe.rating)
                mainmyrecipeView.text = recipe.viewCount.toString()
                mainmyrecipeYours.visibility = if (activeUserID == recipe.userID)
                    View.VISIBLE
                else
                    View.GONE
            }
        }
    }

    inner class RecipeMyListViewHolder(val binding: RecipemylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                binding.root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ChefImageLoader.loadRecipeImage(
                    context,
                    recipe.recipeImg,
                    recipemylistRecipeimg
                )
                recipemylistRecipetitle.text = recipe.recipeName
                recipemylistNickname.text =
                    String.format(
                        context.resources.getString(R.string.format_usernickname),
                        recipe.nickname
                    )
                recipemylistDate.text = Utils.convertMillisToText(recipe.datetime)
                recipemylistRating.text = String.format("%.1f", recipe.rating)
                recipemylistView.text = recipe.viewCount.toString()

                recipemylistYours.visibility = if (activeUserID == recipe.userID)
                    View.VISIBLE
                else
                    View.GONE
            }
        }
    }

    inner class RecipeThemeViewHolder(val binding: RecipethemeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                binding.root.setOnClickListener {
                    itemClickListener!!(recipe)
                }

                ChefImageLoader.loadRecipeImage(
                    context, recipe.recipeImg, recipethemeImg
                )
                recipethemeTitle.text = recipe.recipeName
                recipethemeNickname.text = String.format(
                    context.resources.getString(R.string.format_usernickname),
                    recipe.nickname
                )
                recipethemeRating.text = String.format("%.1f", recipe.rating)
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
