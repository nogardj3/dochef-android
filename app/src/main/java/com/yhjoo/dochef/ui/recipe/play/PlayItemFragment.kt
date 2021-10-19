package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipeplayItemFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment

class PlayItemFragment : BaseFragment() {
    private lateinit var binding: RecipeplayItemFragmentBinding
    private val recipePlayViewModel: RecipePlayViewModel by activityViewModels{
        RecipePlayViewModelFactory(
            RecipeRepository(requireContext().applicationContext),
            ReviewRepository(requireContext().applicationContext),
            null
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_item_fragment, container, false)

        val position = requireArguments().getInt("position")
        val phaseItem = recipePlayViewModel.recipePhase[position]

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recipePhase = phaseItem

            recipeplayItemTips.removeAllViews()
            for (text in phaseItem.tips) {
                val tiptext =
                    layoutInflater.inflate(R.layout.view_tip, null) as AppCompatTextView
                tiptext.text = text
                recipeplayItemTips.addView(tiptext)
            }

            recipeplayItemIngredients.removeAllViews()
            for (ingredient in phaseItem.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.view_ingredient,
                        recipeplayItemIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_amount)
                ingredientAmount.text = ingredient.amount
                recipeplayItemIngredients.addView(ingredientContainer)
            }
        }

        return binding.root
    }
}