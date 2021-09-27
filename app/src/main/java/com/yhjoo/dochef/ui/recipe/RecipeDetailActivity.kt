package com.yhjoo.dochef.ui.recipe

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
import com.yhjoo.dochef.databinding.RecipedetailActivityBinding
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.ui.HomeActivity
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.recipe.play.RecipePlayActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.data.network.RetrofitServices.RecipeService
import com.yhjoo.dochef.data.network.RetrofitServices.ReviewService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeDetailActivity : BaseActivity() {
    /*
        TODO
        recipe revise, delete 추가
        review userdetail 넘어가기 확인
    */

    private val binding: RecipedetailActivityBinding by lazy {
        RecipedetailActivityBinding.inflate(
            layoutInflater
        )
    }

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

        userID = DatastoreUtil.getUserBrief(this).userID
        recipeID = intent.getIntExtra("recipeID", 0)

        binding.apply {
            reviewListAdapter = ReviewListAdapter().apply {
                onItemChildClickListener =
                    BaseQuickAdapter.OnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                        val intent = Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                            .putExtra("userID", (adapter.data[position] as Review).userID)
                        startActivity(intent)
                    }
            }
            recipedetailReviewRecycler.layoutManager =
                object : LinearLayoutManager(this@RecipeDetailActivity) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }

                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            recipedetailReviewRecycler.adapter = reviewListAdapter
        }
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

    private fun loadData() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = recipeService.getRecipeDetail(recipeID)
            recipeDetailInfo = res1.body()!!

            val res2 = reviewService.getReview(recipeID)
            reviewList = res2.body()!!

            setTopView()
            reviewListAdapter.apply {
                setNewData(reviewList)
                setEmptyView(
                    R.layout.review_item_empty,
                    binding.recipedetailReviewRecycler.parent as ViewGroup
                )
            }
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun setTopView() {
        binding.apply {
            ImageLoaderUtil.loadRecipeImage(
                this@RecipeDetailActivity,
                recipeDetailInfo.recipeImg,
                recipedetailMainImg
            )
            ImageLoaderUtil.loadUserImage(
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
                    Intent(this@RecipeDetailActivity, RecipePlayActivity::class.java)
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
                    layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tagrecipe_contents)
                tagview.text = "#$tag"
                recipedetailTags.addView(tagcontainer)
            }

            recipedetailIngredients.removeAllViews()
            for (ingredient in recipeDetailInfo.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(R.layout.view_ingredient, null) as ConstraintLayout
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

    private fun addCount() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            recipeService.addCount(recipeID)
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun setLike() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val like = if (recipeDetailInfo.likes.contains(userID)) -1 else 1
            recipeService.setLikeRecipe(recipeID, userID!!, like)
            loadData()
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}