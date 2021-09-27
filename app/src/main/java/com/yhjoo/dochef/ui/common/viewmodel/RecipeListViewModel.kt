package com.yhjoo.dochef.ui.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    val allRecipeList = MutableLiveData<List<Recipe>>()

    fun requestRecipeList(searchby: Int, sort: String, searchValue: String?) {
        viewModelScope.launch {
            repository.getRecipeList(searchby, sort, searchValue).collect {
                allRecipeList.value = it.body()
            }
        }
    }

    fun addCount(recipeId: Int) {

    }

    fun likeRecipe(recipeId: Int, userId: String) {

    }

    fun disLikeRecipe(recipeId: Int, userId: String) {

    }
}

class RecipeListViewModelFactory(private val repository: RecipeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
            return RecipeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}