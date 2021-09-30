package com.yhjoo.dochef.ui.search

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.SearchResultRecipeItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class RecipeAdapter(
    private val layoutType: Int,
    private val itemClickListener: (Recipe) -> Unit
) :
    ListAdapter<Recipe, RecipeAdapter.ResultRecipeViewHolder>(ResultRecipeComparator()) {
    companion object CONSTANTS {
        object LAYOUT_TYPE {
            const val NAME = 0
            const val INGREDIENT = 1
            const val TAG = 2
        }
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultRecipeViewHolder {
        context = parent.context

        return ResultRecipeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.search_result_recipe_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ResultRecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultRecipeViewHolder(val binding: SearchResultRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(recipe)
                }

                ImageLoaderUtil.loadRecipeImage(
                    context, recipe.recipeImg, resultrecipeRecipeimg
                )

                resultrecipeTitle.text = recipe.recipeName
                resultrecipeNickname.text = String.format(
                    context.resources.getString(R.string.format_usernickname),
                    recipe.nickname
                )

                when (layoutType) {
                    LAYOUT_TYPE.NAME -> {
                        resultrecipeTitle.setTypeface(null, Typeface.BOLD)
                    }
                    LAYOUT_TYPE.INGREDIENT -> {
                        resultrecipeIngredients.isVisible = true
                        resultrecipeIngredients.removeAllViews()

                        for (ingredient in recipe.ingredients) {
                            val tagcontainer =
                                LayoutInflater.from(context)
                                    .inflate(R.layout.view_tag_search, null) as LinearLayout
                            val tagview: AppCompatTextView =
                                tagcontainer.findViewById(R.id.tag_search_text)

                            tagview.text = "#${ingredient.name}"

                            resultrecipeIngredients.addView(tagcontainer)
                        }
                    }
                    LAYOUT_TYPE.TAG -> {
                        resultrecipeTags.isVisible = true
                        resultrecipeTags.removeAllViews()

                        for (tag in recipe.tags) {
                            val tagcontainer =
                                LayoutInflater.from(context)
                                    .inflate(R.layout.view_tag_search, null) as LinearLayout
                            val tagview: AppCompatTextView =
                                tagcontainer.findViewById(R.id.tag_search_text)
                            tagview.text = "#$tag"

                            resultrecipeIngredients.addView(tagcontainer)
                        }
                    }
                }
            }
        }
    }

    class ResultRecipeComparator : DiffUtil.ItemCallback<Recipe>() {
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