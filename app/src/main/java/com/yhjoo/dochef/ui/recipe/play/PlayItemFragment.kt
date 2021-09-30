package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipeplayItemFragmentBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class PlayItemFragment : Fragment() {
    private lateinit var binding: RecipeplayItemFragmentBinding
    private val recipePlayViewModel: RecipePlayViewModel by activityViewModels {
        RecipePlayViewModelFactory(
            RecipeRepository(requireContext().applicationContext),
            ReviewRepository(requireContext().applicationContext)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_item_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            val position = requireArguments().getInt("position")

            recipePlayViewModel.recipePhases.observe(viewLifecycleOwner, { list ->
                val recipePhase = list[position]
                ImageLoaderUtil.loadRecipeImage(
                    requireContext(),
                    recipePhase.recipe_img,
                    recipeplayItemImg
                )
                recipeplayItemTips.removeAllViews()
                for (text in recipePhase.tips) {
                    val tiptext =
                        layoutInflater.inflate(R.layout.view_tip, null) as AppCompatTextView
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
                        ingredientContainer.findViewById(R.id.ingredient_name)
                    ingredientName.text = ingredient.name
                    val ingredientAmount: AppCompatTextView =
                        ingredientContainer.findViewById(R.id.ingredient_amount)
                    ingredientAmount.text = ingredient.amount
                    recipeplayItemIngredients.addView(ingredientContainer)
                }
                recipeplayItemContents.text = recipePhase.contents
            })
        }

        return binding.root
    }
}