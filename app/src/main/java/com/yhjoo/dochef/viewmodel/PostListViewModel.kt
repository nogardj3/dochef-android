package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.repository.PostListRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostListViewModel(
    private val repository: PostListRepository
) : ViewModel() {
    val allPostList = MutableLiveData<List<Post>>()

    init {
        requestPostList()
    }

    fun requestPostList() {
        viewModelScope.launch {
            repository.getPostList().collect {
                allPostList.value = it.body()
            }
        }
    }
}

class PostListViewModelFactory(private val repository: PostListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
            return PostListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}