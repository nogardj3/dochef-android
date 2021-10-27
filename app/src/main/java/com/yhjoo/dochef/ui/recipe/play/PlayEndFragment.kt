package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipeplayEndFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect

class PlayEndFragment : BaseFragment() {
    private lateinit var binding: RecipeplayEndFragmentBinding
    private val recipePlayViewModel: RecipePlayViewModel by activityViewModels {
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
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_end_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = recipePlayViewModel

            recipeplayEndTips.removeAllViews()
            for (text in recipePlayViewModel.endPhase.tips) {
                val tiptext =
                    layoutInflater.inflate(R.layout.view_tip, null) as AppCompatTextView
                tiptext.text = text
                binding.recipeplayEndTips.addView(tiptext)
            }

            recipeplayEndIngredients.removeAllViews()
            for (ingredient in recipePlayViewModel.endPhase.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.view_ingredient,
                        binding.recipeplayEndIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_amount)
                ingredientAmount.text = ingredient.amount
                binding.recipeplayEndIngredients.addView(ingredientContainer)
            }
        }

        subscribeEventOnLifecycle {
            recipePlayViewModel.eventResult.collect {
                if (it.first == RecipePlayViewModel.Events.REVIEW_CREATED) {
                    App.showToast("리뷰가 등록되었습니다.")
                    requireActivity().finish()
                }
            }
        }

        return binding.root
    }
}