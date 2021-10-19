package com.yhjoo.dochef.ui.post

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class PostWriteViewModel(
    private val postRepository: PostRepository,
    private val application: Application,
    intent: Intent,
) : ViewModel() {
    val activeUserId = App.activeUserId
    val currentMode = intent.getIntExtra("MODE", PostWriteActivity.Companion.UIMODE.WRITE)
    val postInfo = intent.getSerializableExtra("post") as Post

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    fun uploadPost(
        imageUri: Uri?,
        contents: String,
        tags: ArrayList<String>
    ) {
        if (imageUri != null) {
            val imageString = String.format(
                application.applicationContext.getString(R.string.format_upload_file),
                activeUserId,
                System.currentTimeMillis().toString()
            )
            val ref =
                storageReference.child(application.applicationContext.getString(R.string.storage_path_post) + imageString)
            ref.putFile(imageUri)
                .addOnSuccessListener {
                    if (currentMode == PostWriteActivity.Companion.UIMODE.WRITE) {
                        createPost(
                            imageString,
                            contents,
                            tags
                        )
                    } else if (currentMode == PostWriteActivity.Companion.UIMODE.REVISE) {
                        updatePost(
                            imageString,
                            contents,
                            tags
                        )
                    }
                }
        } else {
            val imageString = if (postInfo.postImg.isEmpty())
                ""
            else postInfo.postImg

            if (currentMode == PostWriteActivity.Companion.UIMODE.WRITE) {
                createPost(
                    imageString,
                    contents,
                    tags
                )
            } else if (currentMode == PostWriteActivity.Companion.UIMODE.REVISE) {
                updatePost(
                    imageString,
                    contents,
                    tags
                )
            }
        }
    }

    private fun createPost(
        postImgs: String,
        contents: String,
        tags: ArrayList<String>
    ) =
        viewModelScope.launch {
            postRepository.createPost(
                activeUserId,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                _eventResult.emit(Pair(Events.CREATE_COMPLETE, ""))
            }
        }

    private fun updatePost(
        postImgs: String,
        contents: String,
        tags: ArrayList<String>
    ) =
        viewModelScope.launch {
            postRepository.updatePost(
                postInfo.postID,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                _eventResult.emit(Pair(Events.UPDATE_COMPLETE, ""))
            }
        }

    enum class Events {
        CREATE_COMPLETE, UPDATE_COMPLETE
    }
}

class PostWriteViewModelFactory(
    private val postRepository: PostRepository,
    private val application: Application,
    private val intent: Intent,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            return PostWriteViewModel(postRepository, application, intent) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}