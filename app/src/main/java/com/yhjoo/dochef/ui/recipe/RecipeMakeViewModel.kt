package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeMakeViewModel(
    private val recipeRepository: RecipeRepository,
    intent: Intent
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val recipeId: Int = intent.getIntExtra("recipeID", -1)

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

class RecipeMakeViewModelFactory(
    private val recipeRepository: RecipeRepository,
    private val intent: Intent
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeMakeViewModel::class.java)) {
            return RecipeMakeViewModel(
                recipeRepository,
                intent
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}