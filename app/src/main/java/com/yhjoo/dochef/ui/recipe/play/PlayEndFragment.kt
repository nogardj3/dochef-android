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
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipeplayEndFragmentBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class PlayEndFragment : Fragment() {
    private lateinit var binding: RecipeplayEndFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_end_fragment, container, false)
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipePlayViewModel.recipePhases.observe(viewLifecycleOwner, { list ->
                val recipePhase = list.last()

                ImageLoaderUtil.loadRecipeImage(
                    requireContext(),
                    recipePhase.recipe_img,
                    recipeplayEndImg
                )

                recipeplayEndTips.removeAllViews()
                for (text in recipePhase.tips) {
                    val tiptext =
                        layoutInflater.inflate(R.layout.view_tip, null) as AppCompatTextView
                    tiptext.text = text
                    recipeplayEndTips.addView(tiptext)
                }

                recipeplayEndIngredients.removeAllViews()
                for (ingredient in recipePhase.ingredients) {
                    val ingredientContainer =
                        layoutInflater.inflate(
                            R.layout.view_ingredient,
                            recipeplayEndIngredients,
                            false
                        ) as ConstraintLayout
                    val ingredientName: AppCompatTextView =
                        ingredientContainer.findViewById(R.id.ingredient_name)
                    ingredientName.text = ingredient.name
                    val ingredientAmount: AppCompatTextView =
                        ingredientContainer.findViewById(R.id.ingredient_amount)
                    ingredientAmount.text = ingredient.amount
                    recipeplayEndIngredients.addView(ingredientContainer)
                }
                recipeplayEndContents.text = recipePhase.contents

            })

            recipePlayViewModel.recipeDetail.observe(viewLifecycleOwner, {item->
                recipeplayEndReviewOk.setOnClickListener {
                    recipePlayViewModel.createReview(
                        item.recipeID,
                        recipePlayViewModel.userId.value!!,
                        binding.recipeplayEndReviewEdittext.text.toString(),
                        binding.recipeplayEndRating.rating.toLong(), System.currentTimeMillis()
                    )
                }

                recipePlayViewModel.likeThisRecipe.value = item.likes.contains(recipePlayViewModel.userId.value!!)
            })

            recipePlayViewModel.reviewFinished.observe(viewLifecycleOwner, {
                if(it){
                    App.showToast("리뷰가 등록되었습니다.")
                    requireActivity().finish()
                }
            })

            recipePlayViewModel.likeThisRecipe.observe(viewLifecycleOwner, {item->
                recipeplayEndLike.setImageResource(
                    if (item)
                        R.drawable.ic_favorite_red
                    else
                        R.drawable.ic_favorite_black
                )

                recipeplayEndLike.setOnClickListener {
                    recipePlayViewModel.toggleLikeRecipe()
                }
            })
        }
        return view
    }
}