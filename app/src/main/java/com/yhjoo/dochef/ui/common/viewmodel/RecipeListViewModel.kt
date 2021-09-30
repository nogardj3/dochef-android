package com.yhjoo.dochef.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val application: Application,
    private val repository: RecipeRepository
) : ViewModel() {
    private var _listChanged = MutableLiveData<Boolean>()
    val listChanged: LiveData<Boolean>
        get() = _listChanged
    private var _allRecipeList = MutableLiveData<List<Recipe>>()
    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    fun requestRecipeList(searchby: Int, sort: String, searchValue: String?) =
        viewModelScope.launch {
            repository.getRecipeList(searchby, sort, searchValue).collect {
                _allRecipeList.postValue(it.body())
            }
        }

    fun disLikeRecipe(recipeId: Int, userId: String) =
        viewModelScope.launch {
            repository.dislikeRecipe(recipeId, userId).collect {
                _listChanged.postValue(true)
            }
        }
}

class RecipeListViewModelFactory(
    private val application: Application,
    private val repository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
            return RecipeListViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}