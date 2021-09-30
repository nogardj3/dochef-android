package com.yhjoo.dochef.ui.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.CommentRepository
import com.yhjoo.dochef.data.repository.PostRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    val userId = MutableLiveData<String>()
    val postId = MutableLiveData<Int>()
    val isDeleted = MutableLiveData<Boolean>()
    val likeThisPost = MutableLiveData<Boolean>()
    val postDetail = MutableLiveData<Post>()
    val allComments = MutableLiveData<List<Comment>>()

    fun requestPostDetail() {
        viewModelScope.launch {
            postRepository.getPostDetail(postId.value!!).collect {
                postDetail.value = it.body()
            }
        }
    }

    fun toggleLikePost() {
        viewModelScope.launch {
            val like = if (likeThisPost.value!!)
                1
            else
                -1

            if (like == 1)
                postRepository.dislikePost(userId.value!!,postDetail.value!!.postID).collect {
                    likeThisPost.value = false
                }
            else
                postRepository.likePost(userId.value!!,postDetail.value!!.postID).collect {
                    likeThisPost.value = true
                }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            postRepository.deletePost(
                postDetail.value!!.postID
            ).collect {
                isDeleted.value = true
            }
        }
    }

    fun requestComments() {
        viewModelScope.launch {
            commentRepository.getComments(postId.value!!).collect {
                allComments.value = it.body()
            }
        }
    }

    fun createComment(contents: String) {
        viewModelScope.launch {
            commentRepository.createComment(
                postId.value!!,
                userId.value!!,
                contents,
                System.currentTimeMillis()
            ).collect {
                requestComments()
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            commentRepository.deleteComment(
                commentId,
            ).collect {
                requestComments()
            }
        }
    }
}

class PostDetailViewModelFactory(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(postRepository, commentRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}