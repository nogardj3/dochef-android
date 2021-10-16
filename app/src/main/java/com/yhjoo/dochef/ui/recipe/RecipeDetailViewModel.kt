package com.yhjoo.dochef.ui.recipe

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val application: Application,
    intent: Intent,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    private val recipeId: Int = intent.getIntExtra("recipeID", -1)

    val isDeleted = MutableLiveData<Boolean>()

    val recipeDetail = MutableLiveData<RecipeDetail>()
    val allReviews = MutableLiveData<List<Review>>()

    init {
        requestRecipeDetail()
        requestReviews()
        addCount()
    }

    private fun requestRecipeDetail() {
        viewModelScope.launch {
            recipeRepository.getRecipeDetail(recipeId).collect {
                recipeDetail.value = it.body()
            }
        }
    }

    private fun requestReviews() {
        viewModelScope.launch {
            reviewRepository.getReviews(recipeId).collect {
                allReviews.value = it.body()
            }
        }
    }

    private fun addCount() {
        viewModelScope.launch {
            recipeRepository.addCount(recipeId).collect {}
        }
    }

    fun toggleLikeRecipe(like: Int) {
        viewModelScope.launch {
            if (like == 1)
                recipeRepository.likeRecipe(recipeId, activeUserId).collect {
                    requestRecipeDetail()
                }
            else
                recipeRepository.dislikeRecipe(recipeId, activeUserId).collect {
                    requestRecipeDetail()
                }
        }
    }

    fun deleteRecipe() {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipeId, activeUserId).collect {
                if (it.isSuccessful)
                    isDeleted.value = true
            }
        }
    }
}

class RecipeDetailViewModelFactory(
    private val application: Application,
    private val intent: Intent,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(
                application,
                intent,
                recipeRepository,
                reviewRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}