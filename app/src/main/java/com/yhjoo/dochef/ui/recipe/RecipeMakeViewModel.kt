package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeMakeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val recipeId = savedStateHandle.get<Int>(Constants.INTENTNAME.POST_ID)!!

    private val _recipeDetail = MutableLiveData<RecipeDetail>()
    val recipeDetail: LiveData<RecipeDetail>
        get() = _recipeDetail

    init {
        viewModelScope.launch {
            requestRecipeDetail()
        }
    }

    private suspend fun requestRecipeDetail() = withContext(Dispatchers.Main) {
        recipeRepository.getRecipeDetail(recipeId).collect {
            _recipeDetail.value = it.body()
        }
    }

//    private fun createRecipe() {
//        viewModelScope.launch {
//            recipeRepository.createRecipe(recipeId).collect {
//                isFinished.value = true
//            }
//        }
//    }

//    private fun updateRecipe() {
//        viewModelScope.launch {
//            recipeRepository.updateRecipe(recipeId).collect {
//                isFinished.value = true
//            }
//        }
//    }
}