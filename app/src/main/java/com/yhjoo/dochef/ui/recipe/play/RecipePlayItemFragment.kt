package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.RecipeplayItemFragmentBinding
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.utils.ImageLoaderUtil

class RecipePlayItemFragment : Fragment() {
    private lateinit var binding: RecipeplayItemFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecipeplayItemFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val recipePhase = requireArguments().getSerializable("item") as RecipePhase

        binding.apply {
            ImageLoaderUtil.loadRecipeImage(
                requireContext(),
                recipePhase.recipe_img,
                recipeplayItemImg
            )
            recipeplayItemTips.removeAllViews()
            for (text in recipePhase.tips) {
                val tiptext = layoutInflater.inflate(R.layout.view_tip, null) as AppCompatTextView
                tiptext.text = text
                recipeplayItemTips.addView(tiptext)
            }

            recipeplayItemIngredients.removeAllViews()
            for (ingredient in recipePhase.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.view_ingredient,
                        recipeplayItemIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_amount)
                ingredientAmount.text = ingredient.amount
                recipeplayItemIngredients.addView(ingredientContainer)
            }
            recipeplayItemContents.text = recipePhase.contents
        }

        return view
    }
}