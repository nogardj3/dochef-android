package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.RecipeDetail
import com.yhjoo.dochef.model.RecipePhase
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import com.yhjoo.dochef.utilities.RetrofitServices.ReviewService
import com.yhjoo.dochef.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayRecipeEndFragment : Fragment() {
    /*
        TODO
        1. 분기 - end용 처리하기
        2. 리뷰작성 기능
    */

    private lateinit var binding: FPlayrecipeItemBinding
    private lateinit var recipeService: RecipeService
    private lateinit var reviewService: ReviewService
    private lateinit var recipePhase: RecipePhase
    private lateinit var recipeDetail: RecipeDetail
    private var isLikeThis = false
    private var userID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)
        reviewService = RetrofitBuilder.create(requireContext(), ReviewService::class.java)

        userID = Utils.getUserBrief(requireContext()).userID
        recipePhase = requireArguments().getSerializable("item") as RecipePhase
        recipeDetail = requireArguments().getSerializable("item2") as RecipeDetail

        isLikeThis = recipeDetail.likes.contains(userID)

        loadData()
        return view
    }

    private fun loadData() {
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
                    layoutInflater.inflate(R.layout.v_ingredient, playrecipeItemIngredients,false) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_name)
                ingredientName.text = ingredient.name
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_amount)
                ingredientAmount.text = ingredient.amount
                playrecipeItemIngredients.addView(ingredientContainer)
            }
            playrecipeItemContents.text = recipePhase.contents
            playrecipeEndGroup.visibility = View.VISIBLE

            playrecipeEndLike.setImageResource(
                if (isLikeThis) R.drawable.ic_favorite_red
                else R.drawable.ic_favorite_black
            )

            playrecipeEndReviewOk.setOnClickListener { addReview() }
        }
    }

    private fun addReview() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {

            if (binding.playrecipeEndReviewEdittext.text.toString() != "") {
                reviewService.createReview(
                    recipeDetail.recipeID, userID!!,
                    binding.playrecipeEndReviewEdittext.text.toString(),
                    binding.playrecipeEndRating.rating.toLong(), System.currentTimeMillis()
                )
                App.showToast("리뷰가 등록되었습니다.")
                (requireActivity() as BaseActivity?)!!.finish()

            } else App.showToast("댓글을 입력 해 주세요")
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun setLike() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val like = if (isLikeThis) -1 else 1
            recipeService.setLikeRecipe(recipeDetail.recipeID, userID!!, like)
            isLikeThis = !isLikeThis
            loadData()
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}