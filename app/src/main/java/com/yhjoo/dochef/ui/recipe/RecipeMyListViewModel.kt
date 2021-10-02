package com.yhjoo.dochef.ui.recipe

import android.app.Application
import androidx.lifecycle.*
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeMyListViewModel(
    private val application: Application,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }

    private var _allRecipeList = MutableLiveData<List<Recipe>>()

    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    init{
        requestRecipeList(
            Constants.RECIPE.SEARCHBY.USERID,
            Constants.RECIPE.SORT.LATEST,
            activeUserId
        )
    }

    fun requestRecipeList(searchby: Int, sort: String, searchValue: String?) =
        viewModelScope.launch {
            recipeRepository.getRecipeList(searchby, sort, searchValue).collect {
                _allRecipeList.postValue(it.body())
            }
        }

    fun disLikeRecipe(recipeId: Int, userId: String) =
        viewModelScope.launch {
            recipeRepository.dislikeRecipe(recipeId, userId).collect {
                requestRecipeList(
                    Constants.RECIPE.SEARCHBY.USERID,
                    Constants.RECIPE.SORT.LATEST,
                    activeUserId
                )
            }
        }
}

class RecipeMyListViewModelFactory(
    private val application: Application,
    private val repository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeMyListViewModel::class.java)) {
            return RecipeMyListViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}