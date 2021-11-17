package com.yhjoo.dochef.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    var activeUserId = App.activeUserId

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
        viewModelScope.launch {
            requestRecommendList()
            requestActiveUserDetail()
        }

        refreshMyrecipesList()
        refreshRecipesList()
        refreshPostList()
    }

    private suspend fun requestRecommendList() = withContext(Dispatchers.Main) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.USERID,
            Constants.RECIPE.SORT.POPULAR, Constants.adminUserId
        ).collect {
            _allRecommendList.value = it.body()
        }
    }

    private suspend fun requestActiveUserDetail() = withContext(Dispatchers.IO) {
        userRepository.getUserDetail(activeUserId).collect {
            _userDetail.postValue(it.body())
        }
    }

    fun changeRecipesSort(mode: String) {
        recipesSort = mode
        refreshRecipesList()
    }

    fun refreshRecipesList() = viewModelScope.launch(Dispatchers.IO) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.ALL,
            recipesSort, null
        ).collect {
            _allRecipesList.postValue(it.body())
        }
    }

    fun refreshMyrecipesList() = viewModelScope.launch(Dispatchers.IO) {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.USERID,
            recipesSort, activeUserId
        ).collect {
            _allMyrecipeList.postValue(it.body())
        }
    }

    fun refreshPostList() = viewModelScope.launch(Dispatchers.IO) {
        postRepository.getPostList().collect {
            _allTimelines.postValue(it.body())
        }
    }
}