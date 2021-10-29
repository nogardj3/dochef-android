package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeRecommendViewModel(
    private val recipeRepository: RecipeRepository,
    intent: Intent
) : ViewModel() {
    val activeUserId = App.activeUserId
    private val tagName = intent.getStringExtra("tag")?:""

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

class RecipeRecommendViewModelFactory(
    private val repository: RecipeRepository,
    private val intent: Intent
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeRecommendViewModel::class.java)) {
            return RecipeRecommendViewModel(repository, intent) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}