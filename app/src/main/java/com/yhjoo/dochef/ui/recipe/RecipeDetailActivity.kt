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
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.databinding.RecipedetailActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.recipe.play.PlayActivity
import com.yhjoo.dochef.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class RecipeDetailActivity : BaseActivity() {
    // TODO
    // tag, ingredients data binding

    private val binding: RecipedetailActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipedetail_activity)
    }
    private val recipeDetailViewModel: RecipeDetailViewModel by viewModels()

    private lateinit var reviewListAdapter: ReviewListAdapter

    private var powerMenu: PowerMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@RecipeDetailActivity
            activity = this@RecipeDetailActivity
            viewModel = recipeDetailViewModel

            reviewListAdapter = ReviewListAdapter(this@RecipeDetailActivity)

            recipedetailReviewRecycler.also {
                it.layoutManager =
                    object : LinearLayoutManager(this@RecipeDetailActivity) {
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }

                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                it.adapter = reviewListAdapter
            }
        }

        recipeDetailViewModel.recipeDetail.observe(this@RecipeDetailActivity, {
            binding.apply {
                recipedetailTags.removeAllViews()
                for (tag in it.tags) {
                    val tagcontainer =
                        layoutInflater.inflate(R.layout.view_tag_recipe, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_recipe_text)
                    tagview.text = "#$tag"
                    recipedetailTags.addView(tagcontainer)
                }

                recipedetailIngredients.removeAllViews()
                for (ingredient in it.ingredients) {
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
            }
        })

        recipeDetailViewModel.allReviews.observe(this@RecipeDetailActivity, {
            binding.recipedetailReviewEmpty.isVisible = it.isEmpty()
            reviewListAdapter.submitList(it) {}
        })

        subscribeEventOnLifecycle {
            recipeDetailViewModel.eventResult.collect {
                if (it.first == RecipeDetailViewModel.Events.IS_DELETED)
                    finish()
            }
        }
    }

    fun menuShow(item:RecipeDetail){
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
                    0 -> goRecipeMake(item)
                    1 -> recipeDetailViewModel.deleteRecipe()
                }
                powerMenu!!.dismiss()
            }
            .build()
        powerMenu!!.showAsAnchorRightTop(binding.recipedetailOwnermenu)
    }

    fun goHome(userId: String) {
        startActivity(
            Intent(this@RecipeDetailActivity, HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, userId)
        )
    }

    fun goRecipePlay(item: RecipeDetail) {
        startActivity(
            Intent(this@RecipeDetailActivity, PlayActivity::class.java)
                .putExtra("recipe", item)
        )
    }

    private fun goRecipeMake(item: RecipeDetail) {
        startActivity(
            Intent(this@RecipeDetailActivity, RecipeMakeActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
                .putExtra("mode", RecipeMakeActivity.Companion.MODE.REVISE)
        )
    }
}