package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipedetailActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.recipe.play.PlayActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class RecipeDetailActivity : BaseActivity() {
    private val binding: RecipedetailActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipedetail_activity)
    }
    private val recipeDetailViewModel: RecipeDetailViewModel by viewModels {
        RecipeDetailViewModelFactory(
            RecipeRepository(applicationContext),
            ReviewRepository(applicationContext)
        )
    }
    private lateinit var reviewListAdapter: ReviewListAdapter

    private var powerMenu: PowerMenu? = null

    private var userID: String? = null
    private var recipeID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userID = DatastoreUtil.getUserBrief(this).userID
        recipeID = intent.getIntExtra("recipeID", recipeID)

        binding.apply {
            lifecycleOwner = this@RecipeDetailActivity

            reviewListAdapter = ReviewListAdapter { item ->
                Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                    .putExtra("userID", item.userID).apply {
                        startActivity(this)
                    }
            }

            recipedetailReviewRecycler.apply {
                layoutManager =
                    object : LinearLayoutManager(this@RecipeDetailActivity) {
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }

                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                adapter = reviewListAdapter
            }

            recipeDetailViewModel.recipeDetail.observe(this@RecipeDetailActivity, {
                setTopView(it)
            })
            recipeDetailViewModel.allReviews.observe(this@RecipeDetailActivity, {
                reviewListAdapter.submitList(it) {}
            })
            recipeDetailViewModel.isDeleted.observe(this@RecipeDetailActivity, {
                if (it)
                    finish()
            })

            recipeDetailViewModel.addCount(recipeID)
            recipeDetailViewModel.requestRecipeDetail(recipeID)
            recipeDetailViewModel.requestReviews(recipeID)
        }
    }

    private fun setTopView(recipeDetail: RecipeDetail) {
        binding.apply {
            ImageLoaderUtil.loadRecipeImage(
                this@RecipeDetailActivity,
                recipeDetail.recipeImg,
                recipedetailMainImg
            )
            ImageLoaderUtil.loadUserImage(
                this@RecipeDetailActivity,
                recipeDetail.userImg,
                recipedetailUserimg
            )
            recipedetailLike.setImageResource(
                if (recipeDetail.likes.contains(userID) || recipeDetail.userID == userID)
                    R.drawable.ic_favorite_red
                else
                    R.drawable.ic_favorite_black
            )

            recipedetailRecipetitle.text = recipeDetail.recipeName
            recipedetailNickname.text = recipeDetail.nickname
            recipedetailExplain.text = recipeDetail.contents
            recipedetailLikecount.text = recipeDetail.likes.size.toString()
            recipedetailViewcount.text = recipeDetail.viewCount.toString()
            recipedetailReviewRatingText.text = String.format("%.1f", recipeDetail.rating)
            recipedetailReviewRating.rating = recipeDetail.rating

            recipedetailLike.setOnClickListener {
                if (recipeDetail.userID != userID) {
                    val like = if (recipeDetail.likes.contains(userID)) -1 else 1
                    recipeDetailViewModel.toggleLikeRecipe(recipeID, userID!!, like)
                }
            }
            recipedetailStartrecipe.setOnClickListener {
                startActivity(
                    Intent(this@RecipeDetailActivity, PlayActivity::class.java)
                        .putExtra("recipe", recipeDetail)
                )
            }
            recipedetailUserWrapper.setOnClickListener {
                startActivity(
                    Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                        .putExtra("userID", recipeDetail.userID)
                )
            }

            recipedetailTags.removeAllViews()
            for (tag in recipeDetail.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_recipe_text)
                tagview.text = "#$tag"
                recipedetailTags.addView(tagcontainer)
            }

            recipedetailIngredients.removeAllViews()
            for (ingredient in recipeDetail.ingredients) {
                val ingredientContainer =
                    layoutInflater.inflate(R.layout.view_ingredient, null) as ConstraintLayout
                val ingredientName: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_name)
                val ingredientAmount: AppCompatTextView =
                    ingredientContainer.findViewById(R.id.ingredient_amount)

                ingredientName.text = ingredient.name
                ingredientAmount.text = ingredient.amount
                recipedetailIngredients.addView(ingredientContainer)
            }

            recipedetailOwnermenu.isVisible = userID == recipeDetail.userID
            recipedetailOwnermenu.setOnClickListener {
                powerMenu = PowerMenu.Builder(this@RecipeDetailActivity)
                    .addItem(PowerMenuItem("수정", false))
                    .addItem(PowerMenuItem("삭제", false))
                    .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                    .setMenuRadius(10f)
                    .setMenuShadow(0f)
                    .setTextColor(
                        ContextCompat.getColor(
                            this@RecipeDetailActivity,
                            R.color.colorPrimary
                        )
                    )
                    .setSelectedTextColor(Color.WHITE)
                    .setTextGravity(Gravity.CENTER)
                    .setMenuColor(Color.WHITE)
                    .setSelectedMenuColor(
                        ContextCompat.getColor(
                            this@RecipeDetailActivity,
                            R.color.colorPrimary
                        )
                    )
                    .setBackgroundAlpha(0.2f)
                    .setOnMenuItemClickListener { position, _ ->
                        when (position) {
                            0 -> {
                                Intent(this@RecipeDetailActivity, RecipeMakeActivity::class.java)
                                    .putExtra("recipeId", recipeDetail.recipeID)
                                    .putExtra("mode", RecipeMakeActivity.MODE.REVISE).apply {
                                        startActivity(this)
                                    }
                            }
                            1 -> recipeDetailViewModel.deleteRecipe(recipeDetail.recipeID, userID!!)
                        }
                        powerMenu!!.dismiss()
                    }
                    .build()
                powerMenu!!.showAsAnchorRightTop(recipedetailOwnermenu)
            }
        }
    }
}