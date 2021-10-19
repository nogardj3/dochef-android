package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
    intent: Intent
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val recipeId: Int = intent.getIntExtra("recipeID", -1)

    private val _recipeDetail = MutableLiveData<RecipeDetail>()
    private val _allReviews = MutableLiveData<List<Review>>()

    val recipeDetail: LiveData<RecipeDetail>
        get() = _recipeDetail
    val allReviews: LiveData<List<Review>>
        get() = _allReviews

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        requestRecipeDetail()
        requestReviews()
        addCount()
    }

    private fun requestRecipeDetail() = viewModelScope.launch {
        recipeRepository.getRecipeDetail(recipeId).collect {
            _recipeDetail.value = it.body()
        }
    }

    private fun requestReviews() = viewModelScope.launch {
        reviewRepository.getReviews(recipeId).collect {
            _allReviews.value = it.body()
        }
    }

    private fun addCount() = viewModelScope.launch {
        recipeRepository.addCount(recipeId).collect {}
    }

    fun toggleLikeRecipe() = viewModelScope.launch {
        if (_recipeDetail.value!!.likes.contains(activeUserId))
            recipeRepository.dislikeRecipe(recipeId, activeUserId).collect {
                requestRecipeDetail()
            }
        else
            recipeRepository.likeRecipe(recipeId, activeUserId).collect {
                requestRecipeDetail()
            }
    }

    fun deleteRecipe() = viewModelScope.launch {
        recipeRepository.deleteRecipe(recipeId, activeUserId).collect {
            if (it.isSuccessful)
                _eventResult.emit(Pair(Events.IS_DELETED,""))
        }
    }

    enum class Events {
        IS_DELETED,
    }
}

class RecipeDetailViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository,
    private val intent: Intent
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(
                recipeRepository,
                reviewRepository,
                intent
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}