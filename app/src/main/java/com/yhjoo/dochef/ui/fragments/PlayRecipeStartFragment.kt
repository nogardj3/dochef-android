package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.FPlayrecipeStartBinding
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.utils.ImageLoadUtil

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

        ImageLoadUtil.loadRecipeImage(
            requireContext(),
            recipeDetail.recipeImg,
            binding.playrecipeStartImg
        )

        binding.playrecipeStartTitle.text = recipeDetail.recipeName
        binding.playrecipeStartContents.text = recipeDetail.contents

        binding.playrecipeStartTags.removeAllViews()
        for (tag in recipeDetail.tags) {
            val tagcontainer = layoutInflater.inflate(R.layout.v_tag_recipe, null) as LinearLayout
            val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_recipe_text)
            tagview.text = "#$tag"
            binding.playrecipeStartTags.addView(tagcontainer)
        }

        binding.playrecipeStartIngredients.removeAllViews()
        for (ingredient in recipeDetail.ingredients) {
            val ingredientContainer =
                layoutInflater.inflate(R.layout.v_ingredient_play, null) as ConstraintLayout
            val ingredientName: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredientplay_name)
            ingredientName.text = ingredient.name
            val ingredientAmount: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredientplay_amount)
            ingredientAmount.text = ingredient.amount
            binding.playrecipeStartIngredients.addView(ingredientContainer)
        }

        return view
    }
}