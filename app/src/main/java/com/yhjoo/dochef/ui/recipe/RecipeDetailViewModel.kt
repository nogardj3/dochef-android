package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val recipeId = savedStateHandle.get<Int>(Constants.INTENTNAME.RECIPE_ID)!!

    private val _recipeDetail = MutableLiveData<RecipeDetail>()
    private val _allReviews = MutableLiveData<List<Review>>()

    val recipeDetail: LiveData<RecipeDetail>
        get() = _recipeDetail
    val allReviews: LiveData<List<Review>>
        get() = _allReviews

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        viewModelScope.launch {
            requestRecipeDetail()
            requestReviews()
            addCount()
        }
    }

    private suspend fun requestRecipeDetail() = withContext(Dispatchers.Main) {
        recipeRepository.getRecipeDetail(recipeId).collect {
            _recipeDetail.value = it.body()
        }
    }

    private suspend fun requestReviews() = withContext(Dispatchers.Main) {
        reviewRepository.getReviews(recipeId).collect {
            _allReviews.value = it.body()
        }
    }

    private suspend fun addCount() = withContext(Dispatchers.IO) {
        recipeRepository.addCount(recipeId).collect {}
    }

    fun toggleLikeRecipe() = viewModelScope.launch(Dispatchers.IO) {
        if (_recipeDetail.value!!.likes.contains(activeUserId))
            recipeRepository.dislikeRecipe(recipeId, activeUserId).collect {
                requestRecipeDetail()
            }
        else
            recipeRepository.likeRecipe(recipeId, activeUserId).collect {
                requestRecipeDetail()
            }
    }

    fun deleteRecipe() = viewModelScope.launch(Dispatchers.IO) {
        recipeRepository.deleteRecipe(recipeId, activeUserId).collect {
            if (it.isSuccessful)
                _eventResult.emit(Pair(Events.IS_DELETED, ""))
        }
    }

    enum class Events {
        IS_DELETED,
    }
}