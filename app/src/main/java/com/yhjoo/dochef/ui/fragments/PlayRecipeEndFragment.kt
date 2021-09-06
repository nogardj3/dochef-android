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
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.utils.RxRetrofitServices.ReviewService
import com.yhjoo.dochef.utils.Utils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

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

        recipeService = RxRetrofitBuilder.create(requireContext(), RecipeService::class.java)
        reviewService = RxRetrofitBuilder.create(requireContext(), ReviewService::class.java)

        userID = Utils.getUserBrief(requireContext()).userID
        recipePhase = requireArguments().getSerializable("item") as RecipePhase
        recipeDetail = requireArguments().getSerializable("item2") as RecipeDetail

        isLikeThis = recipeDetail.likes.contains(userID)

        loadData()
        return view
    }

    private fun loadData() {
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
        binding.playrecipeEndGroup.visibility = View.VISIBLE

        binding.playrecipeEndLike.setImageResource(
            if (isLikeThis) R.drawable.ic_favorite_red
            else R.drawable.ic_favorite_black
        )

        binding.playrecipeEndReviewOk.setOnClickListener { addReview() }
    }

    private fun addReview() {
        if (binding.playrecipeEndReviewEdittext.text.toString() != "") {
            (activity as BaseActivity).compositeDisposable.add(
                reviewService.createReview(
                    recipeDetail.recipeID, userID!!,
                    binding.playrecipeEndReviewEdittext.text.toString(),
                    binding.playrecipeEndRating.rating.toLong(), System.currentTimeMillis()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        App.showToast("리뷰가 등록되었습니다.")
                        (activity as BaseActivity?)!!.finish()
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else App.showToast("댓글을 입력 해 주세요")
    }

    private fun setLike() {
        val like = if (isLikeThis) -1 else 1
        (activity as BaseActivity).compositeDisposable.add(
            recipeService.setLikeRecipe(recipeDetail.recipeID, userID!!, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isLikeThis = !isLikeThis
                    loadData()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}