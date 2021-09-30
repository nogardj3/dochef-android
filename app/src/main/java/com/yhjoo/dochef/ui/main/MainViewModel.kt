package com.yhjoo.dochef.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val application: Application,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val userId by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }

    private var recipesSort = Constants.RECIPE.SORT.LATEST

    private var _userDetail = MutableLiveData<UserDetail>()
    private var _allRecipesList = MutableLiveData<List<Recipe>>()
    private val _allMyrecipeList = MutableLiveData<List<Recipe>>()
    private val _allRecommendList = MutableLiveData<List<Recipe>>()
    private val _allTimelines = MutableLiveData<List<Post>>()

    val userDetail: LiveData<UserDetail>
        get() = _userDetail
    val allRecipesList: LiveData<List<Recipe>>
        get() = _allRecipesList
    val allMyrecipeList: LiveData<List<Recipe>>
        get() = _allMyrecipeList
    val allRecommendList: LiveData<List<Recipe>>
        get() = _allRecommendList
    val allTimelines: LiveData<List<Post>>
        get() = _allTimelines

    init {
        requestRecommendList()
        refreshMyrecipesList()
        refreshRecipesList()
        requestPostList()
        requestActiveUserDetail()
    }

    fun changeRecipesSort(mode: String) {
        recipesSort = mode
        refreshRecipesList()
    }

    private fun requestRecommendList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.ALL,
                Constants.RECIPE.SORT.POPULAR, null
            ).collect {
                _allRecommendList.value = it.body()
            }
        }
    }

    fun refreshRecipesList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.ALL,
                recipesSort, null
            ).collect {
                _allRecipesList.value = it.body()
            }
        }
    }

    fun refreshMyrecipesList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.USERID,
                recipesSort, userId
            ).collect {
                _allMyrecipeList.value = it.body()
            }
        }
    }

    fun requestPostList() {
        viewModelScope.launch {
            postRepository.getPostList().collect {
                _allTimelines.value = it.body()
            }
        }
    }

    private fun requestActiveUserDetail() {
        viewModelScope.launch {
            userRepository.getUserDetail(userId).collect {
                _userDetail.value = it.body()
            }
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, userRepository, recipeRepository, postRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}