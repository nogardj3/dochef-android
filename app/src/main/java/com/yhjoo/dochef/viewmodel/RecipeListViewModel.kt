package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.repository.PostListRepository
import com.yhjoo.dochef.repository.RecipeListRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val repository: RecipeListRepository
) : ViewModel() {
    val allRecipeList = MutableLiveData<List<Recipe>>()

    fun requestRecipeList(searchby: Int, sort: String, searchValue: String?) {
        viewModelScope.launch {
            repository.getRecipeList(searchby, sort, searchValue).collect {
                allRecipeList.value = it.body()
            }
        }
    }
}

class RecipeListViewModelFactory(private val repository: RecipeListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
            return RecipeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}