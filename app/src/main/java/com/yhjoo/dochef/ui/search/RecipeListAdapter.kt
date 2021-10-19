package com.yhjoo.dochef.ui.search

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

class RecipeListAdapter(
    private val containerActivity: SearchActivity,
    private val resulttype: RESULTTYPE
) :
    ListAdapter<Recipe, RecipeListAdapter.ResultRecipeViewHolder>(ResultRecipeComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultRecipeViewHolder {
        return ResultRecipeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
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
        fun bind(item: Recipe) {
            binding.apply {
                activity = containerActivity
                recipe = item

                when (resulttype) {
                    RESULTTYPE.NAME -> {
                        resultrecipeTitle.setTypeface(null, Typeface.BOLD)
                    }
                    RESULTTYPE.INGREDIENT -> {
                        resultrecipeIngredients.isVisible = true
                        resultrecipeIngredients.removeAllViews()

                        for (ingredient in item.ingredients) {
                            val tagcontainer =
                                LayoutInflater.from(resultrecipeIngredients.context)
                                    .inflate(R.layout.view_tag_search, null) as LinearLayout
                            val tagview: AppCompatTextView =
                                tagcontainer.findViewById(R.id.tag_search_text)

                            tagview.text = "#${ingredient.name}"

                            resultrecipeIngredients.addView(tagcontainer)
                        }
                    }
                    RESULTTYPE.TAG -> {
                        resultrecipeTags.isVisible = true
                        resultrecipeTags.removeAllViews()

                        for (tag in item.tags) {
                            val tagcontainer =
                                LayoutInflater.from(resultrecipeTags.context)
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

    enum class RESULTTYPE {
        NAME, INGREDIENT, TAG
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