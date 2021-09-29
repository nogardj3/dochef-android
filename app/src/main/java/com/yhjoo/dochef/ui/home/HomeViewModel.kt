package com.yhjoo.dochef.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.RECIPE
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    val targetUserId = MutableLiveData<String>()
    val activeUserId = MutableLiveData<String>()
    val userDetail = MutableLiveData<UserDetail>()
    val allRecipes = MutableLiveData<List<Recipe>>()
    val allPosts = MutableLiveData<List<Post>>()

    val updateComplete = MutableLiveData(false)
    val nicknameValid = MutableLiveData(Pair(false, ""))

    fun requestActiveUserDetail() {
        viewModelScope.launch {
            userRepository.getUserDetail(targetUserId.value!!).collect {
                userDetail.value = it.body()
            }
        }
    }

    fun requestRecipeList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                RECIPE.SEARCHBY.USERID,
                RECIPE.SORT.LATEST,
                targetUserId.value!!
            ).collect {
                allRecipes.value = it.body()
            }
        }
    }

    fun requestPostListById() {
        viewModelScope.launch {
            postRepository.getPostListByUserId(targetUserId.value!!).collect {
                allPosts.value = it.body()
            }
        }
    }

    fun updateUser(userImg: String, nickname: String, bio: String) {
        viewModelScope.launch {
            accountRepository.updateUser(
                targetUserId.value!!,
                userImg,
                nickname,
                bio
            )
                .collect {
                    updateComplete.value = true
                }
        }
    }

    fun checkNickname(nickname: String) {
        viewModelScope.launch {
            accountRepository.checkNickname(nickname).collect {
                nicknameValid.value = Pair(it.isSuccessful, nickname)
            }
        }
    }
}

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                userRepository,
                recipeRepository,
                postRepository,
                accountRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}