package com.yhjoo.dochef.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }

    private var _queriedUsers = MutableLiveData<List<UserBrief>>()
    private var _queriedRecipeByName = MutableLiveData<List<Recipe>>()
    private var _queriedRecipeByIngredient = MutableLiveData<List<Recipe>>()
    private var _queriedRecipeByTag = MutableLiveData<List<Recipe>>()

    val queriedUsers: LiveData<List<UserBrief>>
        get() = _queriedUsers
    val queriedRecipeByName: LiveData<List<Recipe>>
        get() = _queriedRecipeByName
    val queriedRecipeByIngredient: LiveData<List<Recipe>>
        get() = _queriedRecipeByIngredient
    val queriedRecipeByTag: LiveData<List<Recipe>>
        get() = _queriedRecipeByTag

    fun searchStart(keyword: String) {
        requestUser(keyword)
        requestRecipeByName(keyword)
        requestRecipeByIngredients(keyword)
        requestRecipeByTag(keyword)
    }

    private fun requestUser(query: String) {
        viewModelScope.launch {
            userRepository.getUserByNickname(
                query
            ).collect {
                _queriedUsers.value = it.body()
            }
        }
    }

    private fun requestRecipeByName(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.RECIPENAME,
                Constants.RECIPE.SORT.POPULAR,
                query
            ).collect {
                _queriedRecipeByName.value = it.body()
            }
        }
    }

    private fun requestRecipeByIngredients(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.INGREDIENT,
                Constants.RECIPE.SORT.POPULAR,
                query
            ).collect {
                _queriedRecipeByIngredient.value = it.body()
            }
        }
    }

    private fun requestRecipeByTag(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.TAG,
                Constants.RECIPE.SORT.POPULAR,
                query
            ).collect {
                _queriedRecipeByTag.value = it.body()
            }
        }
    }
}

class SearchViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(application, userRepository, recipeRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}