package com.yhjoo.dochef.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.utils.OtherUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    val activeUserId = App.activeUserId

    val currentMode = if (savedStateHandle.get<String>(Constants.INTENTNAME.USER_ID) == null
        || savedStateHandle.get<String>(Constants.INTENTNAME.USER_ID) == activeUserId
    )
        HomeActivity.Companion.UIMODE.OWNER
    else HomeActivity.Companion.UIMODE.OTHERS

    private var targetUserId: String =
        if (savedStateHandle.get<String>(Constants.INTENTNAME.USER_ID) == null
            || savedStateHandle.get<String>(Constants.INTENTNAME.USER_ID) == activeUserId
        )
            activeUserId
        else
            savedStateHandle.get<String>(Constants.INTENTNAME.USER_ID)!!

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    private var _userDetail = MutableLiveData<UserDetail>()
    private var _allRecipes = MutableLiveData<List<Recipe>>()
    private var _allPosts = MutableLiveData<List<Post>>()
    private var _isFollowTarget = MutableLiveData<Boolean>()
    val userDetail: LiveData<UserDetail>
        get() = _userDetail
    val allRecipes: LiveData<List<Recipe>>
        get() = _allRecipes
    val allPosts: LiveData<List<Post>>
        get() = _allPosts
    val isFollowTarget: LiveData<Boolean>
        get() = _isFollowTarget

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        requestTargetUserDetail()
        requestRecipeList()
        requestPostListById()
        requestIsFollowTarget()
    }

    private fun requestTargetUserDetail() = viewModelScope.launch {
        userRepository.getUserDetail(targetUserId).collect {
            _userDetail.value = it.body()
        }
    }

    private fun requestRecipeList() = viewModelScope.launch {
        recipeRepository.getRecipeList(
            Constants.RECIPE.SEARCHBY.USERID,
            Constants.RECIPE.SORT.LATEST,
            targetUserId
        ).collect {
            _allRecipes.value = it.body()
        }
    }

    private fun requestPostListById() = viewModelScope.launch {
        postRepository.getPostListByUserId(targetUserId).collect {
            _allPosts.value = it.body()
        }
    }

    private fun requestIsFollowTarget() = viewModelScope.launch {
        userRepository.getUserDetail(activeUserId).collect {
            _isFollowTarget.value = it.body()!!.follow.contains(targetUserId)
        }
    }

    private fun updateUser(userImg: String, nickname: String, bio: String) = viewModelScope.launch {
        accountRepository.updateUser(
            targetUserId,
            userImg,
            nickname,
            bio
        )
            .collect {
                if (it.code() == 200) {
                    _eventResult.emit(Pair(Events.UPDATE_COMPLETE, null))
                    requestTargetUserDetail()
                } else
                    OtherUtil.log(it.code().toString())
            }
    }

    fun toggleSubscribeUser() = viewModelScope.launch {
        if (_isFollowTarget.value == true) {
            userRepository.unsubscribeUser(activeUserId, targetUserId).collect {
                if (it.isSuccessful) {
                    _isFollowTarget.value = false
                    requestTargetUserDetail()
                }
            }
        } else {
            userRepository.subscribeUser(activeUserId, targetUserId).collect {
                if (it.isSuccessful) {
                    _isFollowTarget.value = true
                    requestTargetUserDetail()
                }
            }
        }
    }

    fun checkNickname(nickname: String) = viewModelScope.launch {
        accountRepository.checkNickname(nickname).collect {
            val event = if (it.code() == 200)
                Events.NICKNAME_VALID
            else Events.NICKNAME_INVALID
            _eventResult.emit(Pair(event, nickname))
        }
    }

    fun updateProfile(imageUri: Uri?, nickname: String, profileText: String) =
        viewModelScope.launch {
            if (imageUri != null) {
                val imageUrl = String.format(
                    context.getString(R.string.format_upload_file),
                    activeUserId, System.currentTimeMillis().toString()
                )
                val ref =
                    storageReference.child(context.getString(R.string.storage_path_profile) + imageUrl)
                ref.putFile(imageUri)
                    .addOnSuccessListener {
                        updateUser(
                            imageUrl,
                            nickname,
                            profileText
                        )
                    }
            } else {
                val imageUrl = _userDetail.value!!.userImg
                updateUser(
                    imageUrl,
                    nickname,
                    profileText
                )
            }
        }

    enum class Events {
        UPDATE_COMPLETE, NICKNAME_INVALID, NICKNAME_VALID
    }
}