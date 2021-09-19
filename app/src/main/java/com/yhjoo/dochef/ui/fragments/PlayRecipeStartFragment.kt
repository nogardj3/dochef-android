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
import com.yhjoo.dochef.databinding.FPlayrecipeStartBinding
import com.yhjoo.dochef.model.RecipeDetail
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator

class PlayRecipeStartFragment : Fragment() {
    private lateinit var binding: FPlayrecipeStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FPlayrecipeStartBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val recipeDetail = requireArguments().getSerializable("item") as RecipeDetail

        binding.apply {
            GlideImageLoadDelegator.loadRecipeImage(
                requireContext(),
                recipeDetail.recipeImg,
                playrecipeStartImg
            )

            playrecipeStartTitle.text = recipeDetail.recipeName
            playrecipeStartContents.text = recipeDetail.contents

            playrecipeStartTags.removeAllViews()
            for (tag in recipeDetail.tags) {
                val tagcontainer = layoutInflater.inflate(R.layout.v_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_recipe_text)
                tagview.text = "#$tag"
                playrecipeStartTags.addView(tagcontainer)
            }

            playrecipeStartIngredients.removeAllViews()
            for (ingredient in recipeDetail.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(R.layout.v_ingredient_play,playrecipeStartIngredients, false) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredientplay_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredientplay_amount)
                ingredientAmount.text = ingredient.amount
                playrecipeStartIngredients.addView(ingredientContainer)
            }
        }

        return view
    }
}