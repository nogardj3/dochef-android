package com.yhjoo.dochef.ui.search

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId = App.activeUserId

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

    private suspend fun requestUser(query: String) {
        userRepository.getUserByNickname(
            query
        ).collect {
            _queriedUsers.postValue(it.body())
        }
    }

    private suspend fun requestRecipeByName(query: String) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.RECIPENAME,
            Constants.RECIPE.SORT.POPULAR,
            query
        ).collect {
            _queriedRecipeByName.postValue(it.body())
        }
    }


    private suspend fun requestRecipeByIngredients(query: String) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.INGREDIENT,
            Constants.RECIPE.SORT.POPULAR,
            query
        ).collect {
            _queriedRecipeByIngredient.postValue(it.body())
        }
    }


    private suspend fun requestRecipeByTag(query: String) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.TAG,
            Constants.RECIPE.SORT.POPULAR,
            query
        ).collect {
            _queriedRecipeByTag.postValue(it.body())
        }
    }

    fun searchStart(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestUser(keyword)
            requestRecipeByName(keyword)
            requestRecipeByIngredients(keyword)
            requestRecipeByTag(keyword)
        }
    }
}

class SearchViewModelFactory(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(userRepository, recipeRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}