package com.yhjoo.dochef.ui.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.repository.PostRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class PostWriteViewModel(
    private val postRepository: PostRepository,
) : ViewModel() {
    val userId = MutableLiveData<String>()
    val postId = MutableLiveData<Int>()
    val isFinished = MutableLiveData(false)

    fun createPost(
        postImgs: String,
        contents: String,
        tags: ArrayList<String>
    ) {
        viewModelScope.launch {
            postRepository.createPost(
                userId.value!!,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                isFinished.value = true
            }
        }
    }

    fun updatePost(
        postImgs: String,
        contents: String,
        tags: ArrayList<String>
    ) {
        viewModelScope.launch {
            postRepository.updatePost(
                postId.value!!,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                isFinished.value = true
            }
        }
    }
}

class PostWriteViewModelFactory(
    private val postRepository: PostRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            return PostWriteViewModel(postRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}