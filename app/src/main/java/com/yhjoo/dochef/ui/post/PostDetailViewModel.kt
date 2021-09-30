package com.yhjoo.dochef.ui.post

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.CommentRepository
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val application: Application,
    intent: Intent,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    val userId by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    val postId: Int = intent.getIntExtra("postID", -1)

    private val _isDeleted = MutableLiveData<Boolean>()
    private val _likeThisPost = MutableLiveData<Boolean>()
    private val _postDetail = MutableLiveData<Post>()
    private val _allComments = MutableLiveData<List<Comment>>()

    val isDeleted: LiveData<Boolean>
        get() = _isDeleted
    val likeThisPost: LiveData<Boolean>
        get() = _likeThisPost
    val postDetail: LiveData<Post>
        get() = _postDetail
    val allComments: LiveData<List<Comment>>
        get() = _allComments

    init {
        requestPostDetail()
        requestComments()
    }

    fun requestPostDetail() {
        viewModelScope.launch {
            postRepository.getPostDetail(postId).collect {
                _postDetail.value = it.body()
                _likeThisPost.value = it.body()!!.likes.contains(userId)
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
                postRepository.dislikePost(userId, postDetail.value!!.postID).collect {
                    _likeThisPost.value = false
                }
            else
                postRepository.likePost(userId, postDetail.value!!.postID).collect {
                    _likeThisPost.value = true
                }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            postRepository.deletePost(
                postDetail.value!!.postID
            ).collect {
                _isDeleted.value = true
            }
        }
    }

    fun requestComments() {
        viewModelScope.launch {
            commentRepository.getComments(postId).collect {
                _allComments.value = it.body()
            }
        }
    }

    fun createComment(contents: String) {
        viewModelScope.launch {
            commentRepository.createComment(
                postId,
                userId,
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
    private val application: Application,
    private val intent: Intent,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(application, intent, postRepository, commentRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}