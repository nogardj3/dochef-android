package com.yhjoo.dochef.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.ReviewListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.databinding.ARecipedetailBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.utils.RxRetrofitServices.ReviewService
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import retrofit2.Response
import java.util.*

class RecipeDetailActivity : BaseActivity() {
    var binding: ARecipedetailBinding? = null
    var recipeService: RecipeService? = null
    var reviewService: ReviewService? = null
    var reviewListAdapter: ReviewListAdapter? = null
    var reviewList: ArrayList<Review?>? = null
    var recipeDetailInfo: RecipeDetail? = null
    var userID: String? = null
    var recipeID = 0

    /*
        TODO
        recipe revise, delete 추가
        review userdetail 넘어가기 확인
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ARecipedetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        reviewService = RxRetrofitBuilder.create(this, ReviewService::class.java)
        userID = Utils.getUserBrief(this).userID
        recipeID = intent.getIntExtra("recipeID", 0)
        reviewListAdapter = ReviewListAdapter()
        reviewListAdapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
                val intent = Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                    .putExtra("userID", (adapter.data[position] as Review).userID)
                startActivity(intent)
            }
        binding!!.recipedetailReviewRecycler.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding!!.recipedetailReviewRecycler.adapter = reviewListAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) {
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
            reviewListAdapter!!.setNewData(reviewList)
        }
    }

    fun loadData() {
        recipeService!!.getRecipeDetail(recipeID)
            .flatMap(
                Function { response: Response<RecipeDetail?>? ->
                    recipeDetailInfo = response!!.body()
                    reviewService!!.getReview(recipeID)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                } as Function<Response<RecipeDetail?>?, Single<Response<ArrayList<Review?>?>?>>
            )
            .subscribe({ response: Response<ArrayList<Review?>?>? ->
                reviewList = response!!.body()
                setTopView()
                reviewListAdapter!!.setNewData(reviewList)
                reviewListAdapter!!.setEmptyView(
                    R.layout.rv_empty_review,
                    binding!!.recipedetailReviewRecycler.parent as ViewGroup
                )
            }, RxRetrofitBuilder.defaultConsumer())
    }

    fun setTopView() {
        ImageLoadUtil.loadRecipeImage(
            this,
            recipeDetailInfo.getRecipeImg(),
            binding!!.recipedetailMainImg
        )
        ImageLoadUtil.loadUserImage(
            this,
            recipeDetailInfo.getUserImg(),
            binding!!.recipedetailUserimg
        )
        binding!!.recipedetailRecipetitle.text = recipeDetailInfo.getRecipeName()
        binding!!.recipedetailNickname.text = recipeDetailInfo.getNickname()
        binding!!.recipedetailExplain.text = recipeDetailInfo.getContents()
        binding!!.recipedetailLikecount.text = Integer.toString(recipeDetailInfo.getLikes().size)
        binding!!.recipedetailViewcount.text = Integer.toString(recipeDetailInfo.getView_count())
        binding!!.recipedetailReviewRatingText.text =
            String.format("%.1f", recipeDetailInfo.getRating())
        binding!!.recipedetailReviewRating.rating = recipeDetailInfo.getRating()
        if (recipeDetailInfo.getLikes()
                .contains(userID) || recipeDetailInfo.getUserID() == userID
        ) binding!!.recipedetailLike.setImageResource(R.drawable.ic_favorite_red) else binding!!.recipedetailLike.setImageResource(
            R.drawable.ic_favorite_black
        )
        binding!!.recipedetailLike.setOnClickListener { v: View? -> if (recipeDetailInfo.getUserID() != userID) setLike() }
        binding!!.recipedetailStartrecipe.setOnClickListener { v: View? ->
            Utils.log("wowowowowowoow")
            val intent = Intent(this, PlayRecipeActivity::class.java)
                .putExtra("recipe", recipeDetailInfo)
            startActivity(intent)
        }
        binding!!.recipedetailUserWrapper.setOnClickListener { v: View? ->
            val intent = Intent(this, HomeActivity::class.java)
                .putExtra("userID", recipeDetailInfo.getUserID())
            startActivity(intent)
        }
        binding!!.recipedetailTags.removeAllViews()
        for (tag in recipeDetailInfo.getTags()) {
            val tagcontainer = layoutInflater.inflate(R.layout.v_tag_recipe, null) as LinearLayout
            val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_recipe_text)
            tagview.text = "#$tag"
            binding!!.recipedetailTags.addView(tagcontainer)
        }
        binding!!.recipedetailIngredients.removeAllViews()
        for (ingredient in recipeDetailInfo.getIngredients()) {
            val ingredientContainer =
                layoutInflater.inflate(R.layout.v_ingredient, null) as ConstraintLayout
            val ingredientName: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_name)
            ingredientName.text = ingredient.name
            val ingredientAmount: AppCompatTextView =
                ingredientContainer.findViewById(R.id.v_ingredient_amount)
            ingredientAmount.text = ingredient.amount
            binding!!.recipedetailIngredients.addView(ingredientContainer)
        }
    }

    fun addCount() {
        compositeDisposable!!.add(
            recipeService!!.addCount(recipeID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: Response<JsonObject?>? -> Utils.log("count ok") },
                    RxRetrofitBuilder.defaultConsumer()
                )
        )
    }

    fun setLike() {
        val like = if (recipeDetailInfo.getLikes().contains(userID)) -1 else 1
        compositeDisposable!!.add(
            recipeService!!.setLikeRecipe(recipeID, userID, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: Response<JsonObject?>? -> loadData() },
                    RxRetrofitBuilder.defaultConsumer()
                )
        )
    }
}