package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeMyListViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId = App.activeUserId

    private var _allRecipeList = MutableLiveData<List<Recipe>>()
    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    init {
        requestRecipeList()
    }

    private fun requestRecipeList() =
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.USERID,
                Constants.RECIPE.SORT.LATEST,
                activeUserId
            ).collect {
                _allRecipeList.value = it.body()
            }
        }

    fun disLikeRecipe(recipeId: Int, userId: String) =
        viewModelScope.launch {
            recipeRepository.dislikeRecipe(recipeId, userId).collect {
                requestRecipeList()
            }
        }
}

class RecipeMyListViewModelFactory(
    private val repository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeMyListViewModel::class.java)) {
            return RecipeMyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}