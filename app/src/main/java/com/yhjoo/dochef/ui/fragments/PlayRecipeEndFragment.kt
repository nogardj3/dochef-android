package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.BaseActivity
import com.yhjoo.dochef.databinding.FPlayrecipeItemBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.utils.RxRetrofitServices.ReviewService
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class PlayRecipeEndFragment : Fragment() {
    var binding: FPlayrecipeItemBinding? = null
    var recipeService: RecipeService? = null
    var reviewService: ReviewService? = null
    var recipePhase: RecipePhase? = null
    var recipeDetail: RecipeDetail? = null
    var is_like_this = false
    var userID: String? = null

    /*
        TODO
        1. 분기 - end용 처리하기
        2. 리뷰작성 기능
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FPlayrecipeItemBinding.inflate(inflater, container, false)
        val view: View = binding!!.root
        recipeService = RxRetrofitBuilder.create(context, RecipeService::class.java)
        reviewService = RxRetrofitBuilder.create(context, ReviewService::class.java)
        userID = Utils.getUserBrief(context).userID
        recipePhase = arguments!!.getSerializable("item") as RecipePhase?
        recipeDetail = arguments!!.getSerializable("item2") as RecipeDetail?
        is_like_this = recipeDetail.getLikes().contains(userID)
        loadData()
        return view
    }

    fun loadData() {
        ImageLoadUtil.loadRecipeImage(
            context,
            recipePhase.getRecipe_img(),
            binding!!.playrecipeItemImg
        )
        Utils.log(recipePhase.toString())
        binding!!.playrecipeItemTips.removeAllViews()
        for (text in recipePhase.getTips()) {
            val tiptext = layoutInflater.inflate(R.layout.v_tip, null) as AppCompatTextView
            tiptext.text = text
            binding!!.playrecipeItemTips.addView(tiptext)
        }
        binding!!.playrecipeItemIngredients.removeAllViews()
        for (ingredient in recipePhase.getIngredients()) {
            val ingredientContainer =
                layoutInflater.inflate(R.layout.v_ingredient, null) as ConstraintLayout
            val ingredientName: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_name)
            ingredientName.text = ingredient.name
            val ingredientAmount: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_amount)
            ingredientAmount.text = ingredient.amount
            binding!!.playrecipeItemIngredients.addView(ingredientContainer)
        }
        binding!!.playrecipeItemContents.text = recipePhase.getContents()
        binding!!.playrecipeEndGroup.visibility = View.VISIBLE
        if (is_like_this) binding!!.playrecipeEndLike.setImageResource(R.drawable.ic_favorite_red) else binding!!.playrecipeEndLike.setImageResource(
            R.drawable.ic_favorite_black
        )
        binding!!.playrecipeEndReviewOk.setOnClickListener { v: View? -> addReview(v) }
    }

    fun addReview(v: View?) {
        if (binding!!.playrecipeEndReviewEdittext.text.toString() != "") {
            (activity as BaseActivity?).getCompositeDisposable().add(
                reviewService!!.createReview(
                    recipeDetail.getRecipeID(), userID,
                    binding!!.playrecipeEndReviewEdittext.text.toString(),
                    binding!!.playrecipeEndRating.rating.toLong(), System.currentTimeMillis()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<JsonObject?>? ->
                        appInstance!!.showToast("리뷰가 등록되었습니다.")
                        (activity as BaseActivity?)!!.finish()
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else appInstance!!.showToast("댓글을 입력 해 주세요")
    }

    fun setLike() {
        val like = if (is_like_this) -1 else 1
        (activity as BaseActivity?).getCompositeDisposable().add(
            recipeService!!.setLikeRecipe(recipeDetail.getRecipeID(), userID, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<JsonObject?>? ->
                    is_like_this = !is_like_this
                    loadData()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}