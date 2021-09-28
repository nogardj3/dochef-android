package com.yhjoo.dochef.ui.recipe.play

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.RecipePhase
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipePlayViewModel(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    val userId = MutableLiveData<String>()
    val recipeDetail = MutableLiveData<RecipeDetail>()
    val recipePhases = MutableLiveData<ArrayList<RecipePhase>>()

    fun createReview(
        recipeID: Int,
        userID: String,
        contents: String,
        rating: Long,
        dateTime: Long
    ) {
        viewModelScope.launch {
            reviewRepository.createReview(recipeID, userID, contents, rating, dateTime).collect {}
        }
    }

    fun toggleLikeRecipe(recipeId: Int, userId: String, like: Int) {
        viewModelScope.launch {
            if (like == 1)
                recipeRepository.likeRecipe(recipeId, userId).collect {
                    recipeRepository.getRecipeDetail(recipeDetail.value!!.recipeID)
                }
            else
                recipeRepository.dislikeRecipe(recipeId, userId).collect {
                    recipeRepository.getRecipeDetail(recipeDetail.value!!.recipeID)
                }
        }
    }
}

class RecipePlayViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipePlayViewModel::class.java)) {
            return RecipePlayViewModel(recipeRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}