package com.yhjoo.dochef.ui.recipe

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeMakeViewModel(
    private val application: Application,
    intent: Intent,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    private val recipeId: Int = intent.getIntExtra("recipeID", -1)

    val recipeDetail = MutableLiveData<RecipeDetail>()

    init {
        requestRecipeDetail()
    }

    private fun requestRecipeDetail() {
        viewModelScope.launch {
            recipeRepository.getRecipeDetail(recipeId).collect {
                recipeDetail.value = it.body()
            }
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
    private val application: Application,
    private val intent: Intent,
    private val recipeRepository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeMakeViewModel::class.java)) {
            return RecipeMakeViewModel(
                application,
                intent,
                recipeRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}