package com.yhjoo.dochef.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val userId = MutableLiveData<String>()
    val userDetail = MutableLiveData<UserDetail>()

    private val recipesSort = MutableLiveData<String>(Constants.RECIPE.SORT.LATEST)

    val allRecipesList = MutableLiveData<List<Recipe>>()
    val allMyrecipeList = MutableLiveData<List<Recipe>>()
    val allRecommendList = MutableLiveData<List<Recipe>>()
    val allTimelines = MutableLiveData<List<Post>>()

    fun requestActiveUserDetail() {
        viewModelScope.launch {
            userRepository.getUserDetail(userId.value!!).collect {
                userDetail.value = it.body()
            }
        }
    }

    fun changeRecipesSort(mode: String) {
        recipesSort.postValue(mode)
        refreshRecipesList()
    }

    fun refreshRecipesList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.ALL,
                recipesSort.value!!, null
            ).collect {
                allRecipesList.value = it.body()
            }
        }
    }

    fun refreshMyrecipesList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.USERID,
                recipesSort.value!!, userId.value
            ).collect {
                allMyrecipeList.value = it.body()
            }
        }
    }

    fun requestRecommendList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.ALL,
                Constants.RECIPE.SORT.POPULAR, null
            ).collect {
                allRecommendList.value = it.body()
            }
        }
    }

    fun requestPostList() {
        viewModelScope.launch {
            postRepository.getPostList().collect {
                allTimelines.value = it.body()
            }
        }
    }
}

class MainViewModelFactory(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userRepository, recipeRepository, postRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}