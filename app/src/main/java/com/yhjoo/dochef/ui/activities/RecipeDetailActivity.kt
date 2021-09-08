package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.adapter.ReviewListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.databinding.ARecipedetailBinding
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.RecipeService
import com.yhjoo.dochef.utils.RetrofitServices.ReviewService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.*

class RecipeDetailActivity : BaseActivity() {
    /*
        TODO
        recipe revise, delete 추가
        review userdetail 넘어가기 확인
    */

    private val binding: ARecipedetailBinding by lazy { ARecipedetailBinding.inflate(layoutInflater) }

    private lateinit var recipeService: RecipeService
    private lateinit var reviewService: ReviewService
    private lateinit var reviewListAdapter: ReviewListAdapter
    private lateinit var reviewList: ArrayList<Review>
    private lateinit var recipeDetailInfo: RecipeDetail

    private var userID: String? = null
    private var recipeID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recipeService = RetrofitBuilder.create(this, RecipeService::class.java)
        reviewService = RetrofitBuilder.create(this, ReviewService::class.java)

        userID = Utils.getUserBrief(this).userID
        recipeID = intent.getIntExtra("recipeID", 0)

        reviewListAdapter = ReviewListAdapter().apply {
            onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                    val intent = Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                        .putExtra("userID", (adapter.data[position] as Review).userID)
                    startActivity(intent)
                }
        }
        binding.recipedetailReviewRecycler.layoutManager =
            object : LinearLayoutManager(this) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        binding.recipedetailReviewRecycler.adapter = reviewListAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) {
            addCount()
            loadData()
        } else {
            recipeDetailInfo = DataGenerator.make(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)
            )
            reviewList =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_REVIEW))
            setTopView()
            reviewListAdapter.setNewData(reviewList)
        }
    }

    private fun loadData() {
        recipeService.getRecipeDetail(recipeID)
            .flatMap {
                recipeDetailInfo = it.body()!!
                reviewService.getReview(recipeID)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .subscribe({
                reviewList = it.body()!!
                setTopView()
                reviewListAdapter.setNewData(reviewList)
                reviewListAdapter.setEmptyView(
                    R.layout.rv_empty_review,
                    binding.recipedetailReviewRecycler.parent as ViewGroup
                )
            }, RetrofitBuilder.defaultConsumer())
    }

    private fun setTopView() {
        binding.apply {
            ImageLoadUtil.loadRecipeImage(
                this@RecipeDetailActivity,
                recipeDetailInfo.recipeImg,
                recipedetailMainImg
            )
            ImageLoadUtil.loadUserImage(
                this@RecipeDetailActivity,
                recipeDetailInfo.userImg,
                recipedetailUserimg
            )
            recipedetailLike.setImageResource(
                if (recipeDetailInfo.likes.contains(userID) || recipeDetailInfo.userID == userID)
                    R.drawable.ic_favorite_red
                else
                    R.drawable.ic_favorite_black
            )

            recipedetailRecipetitle.text = recipeDetailInfo.recipeName
            recipedetailNickname.text = recipeDetailInfo.nickname
            recipedetailExplain.text = recipeDetailInfo.contents
            recipedetailLikecount.text = recipeDetailInfo.likes.size.toString()
            recipedetailViewcount.text = recipeDetailInfo.viewCount.toString()
            recipedetailReviewRatingText.text = String.format("%.1f", recipeDetailInfo.rating)
            recipedetailReviewRating.rating = recipeDetailInfo.rating

            recipedetailLike.setOnClickListener { if (recipeDetailInfo.userID != userID) setLike() }
            recipedetailStartrecipe.setOnClickListener {
                startActivity(
                    Intent(this@RecipeDetailActivity, PlayRecipeActivity::class.java)
                        .putExtra("recipe", recipeDetailInfo)
                )
            }
            recipedetailUserWrapper.setOnClickListener {
                startActivity(
                    Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                        .putExtra("userID", recipeDetailInfo.userID)
                )
            }

            recipedetailTags.removeAllViews()
            for (tag in recipeDetailInfo.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.v_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_recipe_text)
                tagview.text = "#$tag"
                recipedetailTags.addView(tagcontainer)
            }

            recipedetailIngredients.removeAllViews()
            for (ingredient in recipeDetailInfo.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(R.layout.v_ingredient, null) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_name)
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.v_ingredient_amount)

                ingredientName.text = ingredient.name
                ingredientAmount.text = ingredient.amount
                recipedetailIngredients.addView(ingredientContainer)
            }
        }
    }

    private fun addCount() {
        compositeDisposable.add(
            recipeService.addCount(recipeID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Utils.log("count ok") },
                    RetrofitBuilder.defaultConsumer()
                )
        )
    }

    private fun setLike() {
        val like = if (recipeDetailInfo.likes.contains(userID)) -1 else 1
        compositeDisposable!!.add(
            recipeService!!.setLikeRecipe(recipeID, userID!!, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadData() },
                    RetrofitBuilder.defaultConsumer()
                )
        )
    }
}