package com.yhjoo.dochef.ui.post

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.R
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
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }

    private val postId: Int = intent.getIntExtra("postID", -1)
    private val postImage: String? = intent.getStringExtra("postImg")
    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    val isFinished = MutableLiveData<Boolean>()

    fun uploadPost(
        currentMode: Int,
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
            val imageString = postImage
            if (currentMode == PostWriteActivity.Companion.UIMODE.WRITE) {
                createPost(
                    imageString!!,
                    contents,
                    tags
                )
            } else if (currentMode == PostWriteActivity.Companion.UIMODE.REVISE) {
                updatePost(
                    imageString!!,
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
    ) {
        viewModelScope.launch {
            postRepository.createPost(
                activeUserId,
                postImgs,
                contents,
                System.currentTimeMillis(),
                tags
            ).collect {
                isFinished.value = true
            }
        }
    }

    private fun updatePost(
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