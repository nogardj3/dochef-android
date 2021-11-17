package com.yhjoo.dochef.ui.post

import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.CommentRepository
import com.yhjoo.dochef.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    val activeUserId = App.activeUserId
    val postId = savedStateHandle.get<Int>(Constants.INTENTNAME.POST_ID)!!

    private val _postDetail = MutableLiveData<Post>()
    private val _allComments = MutableLiveData<List<Comment>>()

    val postDetail: LiveData<Post>
        get() = _postDetail
    val allComments: LiveData<List<Comment>>
        get() = _allComments

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        requestPostDetail()
        requestComments()
    }

    private fun requestPostDetail() = viewModelScope.launch {
        postRepository.getPostDetail(postId).collect {
            _postDetail.value = it.body()
        }
    }

    private fun requestComments() = viewModelScope.launch {
        commentRepository.getComments(postId).collect {
            _allComments.value = it.body()
        }
    }

    fun toggleLikePost() = viewModelScope.launch {
        if (_postDetail.value!!.likes.contains(activeUserId))
            postRepository.dislikePost(activeUserId, postDetail.value!!.postID).collect {
                requestPostDetail()
            }
        else
            postRepository.likePost(activeUserId, postDetail.value!!.postID).collect {
                requestPostDetail()
            }
    }

    fun deletePost() = viewModelScope.launch {
        postRepository.deletePost(
            postDetail.value!!.postID
        ).collect {
            _eventResult.emit(Pair(Events.IS_DELETED, ""))
        }
    }

    fun createComment(contents: String) = viewModelScope.launch {
        commentRepository.createComment(
            postId,
            activeUserId,
            contents,
            System.currentTimeMillis()
        ).collect {
            requestComments()
        }
    }

    fun deleteComment(commentId: Int) = viewModelScope.launch {
        commentRepository.deleteComment(
            commentId,
        ).collect {
            requestComments()
        }
    }

    enum class Events {
        IS_DELETED
    }
}