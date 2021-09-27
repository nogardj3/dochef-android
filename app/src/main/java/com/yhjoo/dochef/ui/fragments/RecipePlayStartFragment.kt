package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.RecipeplayStartFragmentBinding
import com.yhjoo.dochef.model.RecipeDetail
import com.yhjoo.dochef.utilities.ChefImageLoader

class RecipePlayStartFragment : Fragment() {
    private lateinit var binding: RecipeplayStartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecipeplayStartFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val recipeDetail = requireArguments().getSerializable("item") as RecipeDetail

        binding.apply {
            ChefImageLoader.loadRecipeImage(
                requireContext(),
                recipeDetail.recipeImg,
                recipeplayStartImg
            )

            recipeplayStartTitle.text = recipeDetail.recipeName
            recipeplayStartContents.text = recipeDetail.contents

            recipeplayStartTags.removeAllViews()
            for (tag in recipeDetail.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tagrecipe_contents)
                tagview.text = "#$tag"
                recipeplayStartTags.addView(tagcontainer)
            }

            recipeplayStartIngredients.removeAllViews()
            for (ingredient in recipeDetail.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.view_ingredient_play,
                        recipeplayStartIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredientplay_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredientplay_amount)
                ingredientAmount.text = ingredient.amount
                recipeplayStartIngredients.addView(ingredientContainer)
            }
        }

        return view
    }
}