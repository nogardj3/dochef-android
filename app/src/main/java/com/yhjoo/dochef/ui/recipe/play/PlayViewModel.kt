package com.yhjoo.dochef.ui.recipe.play

import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipePlayViewModel(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
    intent: Intent
) : ViewModel() {
    val recipeDetail = intent.getSerializableExtra("recipe") as RecipeDetail
    val recipePhase = recipeDetail.phases
    val endPhase = recipePhase.last()

    val activeUserId = App.activeUserId

    private val _likeThisRecipe = MutableLiveData<Boolean>()
    val likeThisRecipe: LiveData<Boolean>
        get() = _likeThisRecipe

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        _likeThisRecipe.value = recipeDetail.likes.contains(activeUserId)
    }

    fun createReview(
        contents: String,
        rating: Float,
    ) = viewModelScope.launch {
        reviewRepository.createReview(
            recipeDetail.recipeID,
            activeUserId,
            contents,
            rating,
            System.currentTimeMillis()
        ).collect {
            _eventResult.emit(Pair(Events.REVIEW_CREATED, null))
        }
    }

    fun toggleLikeRecipe() = viewModelScope.launch {
        if (likeThisRecipe.value!!)
            recipeRepository.dislikeRecipe(recipeDetail.recipeID, activeUserId)
                .collect {
                    _likeThisRecipe.value = false
                }
        else
            recipeRepository.likeRecipe(recipeDetail.recipeID, activeUserId).collect {
                _likeThisRecipe.value = true
            }
    }

    enum class Events {
        REVIEW_CREATED,
    }
}

class RecipePlayViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
    private val intent: Intent
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipePlayViewModel::class.java)) {
            return RecipePlayViewModel(recipeRepository, reviewRepository, intent) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}