package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipeplayStartFragmentBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil

class PlayStartFragment : Fragment() {
    private lateinit var binding: RecipeplayStartFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.recipeplay_start_fragment, container, false)
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipePlayViewModel.recipeDetail.observe(viewLifecycleOwner, { rdrd ->
                ImageLoaderUtil.loadRecipeImage(
                    requireContext(),
                    rdrd.recipeImg,
                    recipeplayStartImg
                )

                recipeplayStartTitle.text = rdrd.recipeName
                recipeplayStartContents.text = rdrd.contents

                recipeplayStartTags.removeAllViews()
                for (tag in rdrd.tags) {
                    val tagcontainer =
                        layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_recipe_text)
                    tagview.text = "#$tag"
                    recipeplayStartTags.addView(tagcontainer)
                }

                recipeplayStartIngredients.removeAllViews()
                for (ingredient in rdrd.ingredients) {
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
            })

//            val recipeDetail = recipePlayViewModel.recipeDetail.value!!
//            ImageLoaderUtil.loadRecipeImage(
//                requireContext(),
//                recipeDetail.recipeImg,
//                recipeplayStartImg
//            )
//
//            recipeplayStartTitle.text = recipeDetail.recipeName
//            recipeplayStartContents.text = recipeDetail.contents
//
//            recipeplayStartTags.removeAllViews()
//            for (tag in recipeDetail.tags) {
//                val tagcontainer =
//                    layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
//                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_recipe_text)
//                tagview.text = "#$tag"
//                recipeplayStartTags.addView(tagcontainer)
//            }
//
//            recipeplayStartIngredients.removeAllViews()
//            for (ingredient in recipeDetail.ingredients) {
//                val ingredientContainer =
//                    layoutInflater.inflate(
//                        R.layout.view_ingredient_start,
//                        recipeplayStartIngredients,
//                        false
//                    ) as ConstraintLayout
//                val ingredientName: AppCompatTextView =
//                    ingredientContainer.findViewById(R.id.ingredient_start_name)
//                ingredientName.text = ingredient.name
//                val ingredientAmount: AppCompatTextView =
//                    ingredientContainer.findViewById(R.id.ingredient_start_amount)
//                ingredientAmount.text = ingredient.amount
//                recipeplayStartIngredients.addView(ingredientContainer)
//            }
        }

        return view
    }
}