package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.model.RecipePhase
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator

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

        binding.apply {
            GlideImageLoadDelegator.loadRecipeImage(
                requireContext(),
                recipePhase.recipe_img,
                playrecipeItemImg
            )
            playrecipeItemTips.removeAllViews()
            for (text in recipePhase.tips) {
                val tiptext = layoutInflater.inflate(R.layout.v_tip, null) as AppCompatTextView
                tiptext.text = text
                playrecipeItemTips.addView(tiptext)
            }

            playrecipeItemIngredients.removeAllViews()
            for (ingredient in recipePhase.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(
                        R.layout.v_ingredient,
                        playrecipeItemIngredients,
                        false
                    ) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_amount)
                ingredientAmount.text = ingredient.amount
                playrecipeItemIngredients.addView(ingredientContainer)
            }
            playrecipeItemContents.text = recipePhase.contents

            playrecipeEndReviewEdittext.addTextChangedListener(object : TextWatcher {
                var prevText = ""

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    prevText = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (binding.playrecipeEndReviewEdittext.lineCount > 3 || s.toString().length >= 120) {
                        binding.playrecipeEndReviewEdittext.setText(prevText)
                        binding.playrecipeEndReviewEdittext.setSelection(prevText.length - 1)
                    }
                }
            })
        }

        return view
    }
}