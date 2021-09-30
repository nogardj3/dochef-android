package com.yhjoo.dochef.ui.post

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class PostWriteViewModel(
    private val application: Application,
    intent: Intent,
    private val postRepository: PostRepository,
) : ViewModel() {
    val userId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    val postId: Int = intent.getIntExtra("postID", -1)
    private val currentMode: Int =
        intent.getIntExtra("MODE", PostWriteActivity.CONSTANTS.UIMODE.WRITE)
    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    val isFinished = MutableLiveData<Boolean>()

    init{

    }

    fun createPost(
        postImgs: String,
        contents: String,
        tags: ArrayList<String>
    ) {
        viewModelScope.launch {
            postRepository.createPost(
                userId,
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
                postId,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                isFinished.value = true
            }
        }
    }

    fun uploadPost() {

    }
}

class PostWriteViewModelFactory(
    private val application: Application,
    private val intent: Intent,
    private val postRepository: PostRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            return PostWriteViewModel(application, intent, postRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}