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
    val reviewFinished = MutableLiveData<Boolean>()
    val likeThisRecipe = MutableLiveData<Boolean>()
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
            reviewRepository.createReview(recipeID, userID, contents, rating, dateTime).collect {
                reviewFinished.value = true
            }
        }
    }

    fun toggleLikeRecipe() {
        viewModelScope.launch {
            val like = if (likeThisRecipe.value!!)
                1
            else
                -1

            if (like == 1)
                recipeRepository.dislikeRecipe(recipeDetail.value!!.recipeID, userId.value!!).collect {
                    likeThisRecipe.value = false
                }
            else
                recipeRepository.likeRecipe(recipeDetail.value!!.recipeID, userId.value!!).collect {
                    likeThisRecipe.value = true
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