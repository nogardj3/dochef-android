package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeRecommendViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val tagName = savedStateHandle.get<String>("tag")
        ?: ""

    private var _allRecipeList = MutableLiveData<List<Recipe>>()
    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    init {
        viewModelScope.launch {
            requestRecipeList()
        }
    }

    private suspend fun requestRecipeList() {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.TAG,
            Constants.RECIPE.SORT.POPULAR,
            tagName
        ).collect {
            _allRecipeList.value = it.body()
        }
    }
}