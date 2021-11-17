package com.yhjoo.dochef.ui.post

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PostWriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel() {
    val activeUserId = App.activeUserId
    val currentMode: Int? = savedStateHandle.get<Int>("mode")
    val postInfo: Post? = savedStateHandle.get<Post>("post")

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
                context.getString(R.string.format_upload_file),
                activeUserId,
                System.currentTimeMillis().toString()
            )
            val ref =
                storageReference.child(context.getString(R.string.storage_path_post) + imageString)
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
            val imageString = if (postInfo == null || postInfo.postImg.isEmpty())
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
                postInfo!!.postID,
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