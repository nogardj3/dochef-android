package com.yhjoo.dochef.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeMyListViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId = App.activeUserId

    private var _allRecipeList = MutableLiveData<List<Recipe>>()
    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    init {
        viewModelScope.launch {
            requestRecipeList()
        }
    }

    private suspend fun requestRecipeList() =
        withContext(Dispatchers.Main) {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.USERID,
                Constants.RECIPE.SORT.LATEST,
                activeUserId
            ).collect {
                _allRecipeList.value = it.body()
            }
        }

    fun disLikeRecipe(recipeId: Int, userId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.dislikeRecipe(recipeId, userId).collect {
                requestRecipeList()
            }
        }
}