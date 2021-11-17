package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.RecipeplayStartFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment

class PlayStartFragment : BaseFragment() {
    private lateinit var binding: RecipeplayStartFragmentBinding
    private val recipePlayViewModel: RecipePlayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_start_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = recipePlayViewModel

            recipeplayStartTags.removeAllViews()
            for (tag in recipePlayViewModel.recipeDetail.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_recipe_text)
                tagview.text = "#$tag"
                recipeplayStartTags.addView(tagcontainer)
            }

            recipeplayStartIngredients.removeAllViews()
            for (ingredient in recipePlayViewModel.recipeDetail.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.view_ingredient_start,
                        recipeplayStartIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_start_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_start_amount)
                ingredientAmount.text = ingredient.amount
                recipeplayStartIngredients.addView(ingredientContainer)
            }
        }

        return binding.root
    }
}