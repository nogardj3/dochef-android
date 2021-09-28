package com.yhjoo.dochef.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val keyword = MutableLiveData<String?>()
    val queriedUsers = MutableLiveData<List<UserBrief>>()
    val queriedRecipeByName = MutableLiveData<List<Recipe>>()
    val queriedRecipeByIngredient = MutableLiveData<List<Recipe>>()
    val queriedRecipeByTag = MutableLiveData<List<Recipe>>()

    fun requestUser(query: String) {
        viewModelScope.launch {
            userRepository.getUserByNickname(
                query
            ).collect {
                queriedUsers.value = it.body()
            }
        }
    }

    fun requestRecipeByName(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                RecipeRepository.Companion.SEARCHBY.RECIPENAME,
                RecipeListVerticalAdapter.Companion.SORT.POPULAR,
                query
            ).collect {
                queriedRecipeByName.value = it.body()
            }
        }
    }

    fun requestRecipeByIngredients(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                RecipeRepository.Companion.SEARCHBY.INGREDIENT,
                RecipeListVerticalAdapter.Companion.SORT.POPULAR,
                query
            ).collect {
                queriedRecipeByIngredient.value = it.body()
            }
        }
    }

    fun requestRecipeByTag(query: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                RecipeRepository.Companion.SEARCHBY.TAG,
                RecipeListVerticalAdapter.Companion.SORT.POPULAR,
                query
            ).collect {
                queriedRecipeByTag.value = it.body()
            }
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