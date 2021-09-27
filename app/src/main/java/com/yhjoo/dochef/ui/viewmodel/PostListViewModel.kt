package com.yhjoo.dochef.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostListViewModel(
    private val repository: PostRepository
) : ViewModel() {
    val allPostList = MutableLiveData<List<Post>>()

    fun requestPostList() {
        viewModelScope.launch {
            repository.getPostList().collect {
                allPostList.value = it.body()
            }
        }
    }

    fun requestPostListById(userId: String) {
        viewModelScope.launch {
            repository.getPostListByUserId(userId).collect {
                allPostList.value = it.body()
            }
        }
    }


    fun likePost(recipeId: Int, userId: String) {

    }

    fun disLikePost(recipeId: Int, userId: String) {

    }

}

class PostListViewModelFactory(private val repository: PostRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
            return PostListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}