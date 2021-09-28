package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import com.yhjoo.dochef.utils.OtherUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    val recipeDetail = MutableLiveData<RecipeDetail>()
    val allReviews = MutableLiveData<List<Review>>()

    fun addCount(recipeId:Int){
        viewModelScope.launch {
            recipeRepository.addCount(recipeId).collect{}
        }
    }

    fun requestRecipeDetail(recipeId:Int) {
        viewModelScope.launch {
            recipeRepository.getRecipeDetail(recipeId).collect {
                recipeDetail.value = it.body()
            }
        }
    }

    fun requestReviews(recipeId:Int) {
        viewModelScope.launch {
            reviewRepository.getReviews(recipeId).collect {
                allReviews.value = it.body()
            }
        }
    }

    fun toggleLikeRecipe(recipeId:Int, userId: String, like: Int) {
        viewModelScope.launch {
            if (like == 1)
                recipeRepository.likeRecipe(recipeId, userId).collect{
                    requestRecipeDetail(recipeId)
                }
            else
                recipeRepository.dislikeRecipe(recipeId, userId).collect{
                    requestRecipeDetail(recipeId)
                }
        }
    }
}

class RecipeDetailViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(recipeRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}