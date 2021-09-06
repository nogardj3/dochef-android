package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.utils.ImageLoadUtil

class PlayRecipeItemFragment : Fragment() {
    private lateinit var binding: FPlayrecipeItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val recipePhase = requireArguments().getSerializable("item") as RecipePhase

        ImageLoadUtil.loadRecipeImage(
            requireContext(),
            recipePhase.recipe_img,
            binding.playrecipeItemImg
        )

        binding.playrecipeItemTips.removeAllViews()
        for (text in recipePhase.tips) {
            val tiptext = layoutInflater.inflate(R.layout.v_tip, null) as AppCompatTextView
            tiptext.text = text
            binding.playrecipeItemTips.addView(tiptext)
        }

        binding.playrecipeItemIngredients.removeAllViews()
        for (ingredient in recipePhase.ingredients) {
            val ingredientContainer =
                layoutInflater.inflate(R.layout.v_ingredient, null) as ConstraintLayout
            val ingredientName: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_name)
            ingredientName.text = ingredient.name
            val ingredientAmount: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_amount)
            ingredientAmount.text = ingredient.amount
            binding.playrecipeItemIngredients.addView(ingredientContainer)
        }
        binding.playrecipeItemContents.text = recipePhase.contents

        return view
    }
}