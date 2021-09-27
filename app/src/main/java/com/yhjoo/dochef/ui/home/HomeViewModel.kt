package com.yhjoo.dochef.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val activeUserDetail = MutableLiveData<UserDetail>()
    val allRecipeList = MutableLiveData<List<Recipe>>()
    val allPosts = MutableLiveData<List<Post>>()

    fun requestActiveUserDetail() {
        viewModelScope.launch {
            userRepository.getUserDetail().collect {
                activeUserDetail.value = it.body()
            }
        }
    }

    fun requestRecipeList(userId: String) {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                RecipeRepository.Companion.SEARCHBY.USERID,
                RecipeListVerticalAdapter.Companion.SORT.LATEST,
                userId
            ).collect {
                allRecipeList.value = it.body()
            }
        }
    }

    fun requestPostList() {
        viewModelScope.launch {
            postRepository.getPostList().collect {
                allPosts.value = it.body()
            }
        }
    }

    fun requestPostListById(userId: String) {
        viewModelScope.launch {
            postRepository.getPostListByUserId(userId).collect {
                allPosts.value = it.body()
            }
        }
    }

    fun addCount(recipeId: Int) {

    }
}

class RecipeListViewModelFactory(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(userRepository, recipeRepository, postRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}