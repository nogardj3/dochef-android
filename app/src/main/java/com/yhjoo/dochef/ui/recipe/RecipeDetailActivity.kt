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
import com.yhjoo.dochef.data.model.Review
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
            application,
            intent,
            RecipeRepository(applicationContext),
            ReviewRepository(applicationContext)
        )
    }
    private lateinit var reviewListAdapter: ReviewListAdapter

    private var powerMenu: PowerMenu? = null

    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@RecipeDetailActivity

            reviewListAdapter = ReviewListAdapter {
                goHome(it.userID)
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
                recipedetailReviewEmpty.isVisible = it.isEmpty()
                reviewListAdapter.submitList(it) {}
            })
            recipeDetailViewModel.isDeleted.observe(this@RecipeDetailActivity, {
                if (it)
                    finish()
            })
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
                    recipeDetailViewModel.toggleLikeRecipe(like)
                }
            }
            recipedetailStartrecipe.setOnClickListener {
                goRecipePlay(recipeDetail)
            }
            recipedetailUserWrapper.setOnClickListener {
                goHome(recipeDetail.userID)
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
                            0 -> goRecipeMake(recipeDetail)
                            1 -> recipeDetailViewModel.deleteRecipe()
                        }
                        powerMenu!!.dismiss()
                    }
                    .build()
                powerMenu!!.showAsAnchorRightTop(recipedetailOwnermenu)
            }
        }
    }

    private fun goHome(userId: String) {
        startActivity(
            Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                .putExtra("userID", userId)
        )
    }

    private fun goRecipePlay(item: RecipeDetail) {
        startActivity(
            Intent(this@RecipeDetailActivity, PlayActivity::class.java)
                .putExtra("recipe", item)
        )
    }

    private fun goRecipeMake(item: RecipeDetail) {
        startActivity(
            Intent(this@RecipeDetailActivity, RecipeMakeActivity::class.java)
                .putExtra("recipeId", item.recipeID)
                .putExtra("mode", RecipeMakeActivity.MODE.REVISE)
        )
    }
}