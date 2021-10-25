package com.yhjoo.dochef.ui.home

import android.app.Application
import android.content.Intent
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository,
    private val application: Application,
    intent: Intent,
) : ViewModel() {
    val activeUserId = App.activeUserId

    val currentMode = if (intent.getStringExtra("userID") == null
        || intent.getStringExtra("userID") == activeUserId
    )
        HomeActivity.UIMODE.OWNER
    else HomeActivity.UIMODE.OTHERS

    private var targetUserId: String =
        if (intent.getStringExtra("userID") == null
            || intent.getStringExtra("userID") == activeUserId
        )
            activeUserId
        else
            intent.getStringExtra("userID")!!

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    private var _userDetail = MutableLiveData<UserDetail>()
    private var _allRecipes = MutableLiveData<List<Recipe>>()
    private var _allPosts = MutableLiveData<List<Post>>()
    val userDetail: LiveData<UserDetail>
        get() = _userDetail
    val allRecipes: LiveData<List<Recipe>>
        get() = _allRecipes
    val allPosts: LiveData<List<Post>>
        get() = _allPosts

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        requestActiveUserDetail()
        requestRecipeList()
        requestPostListById()
    }

    private fun requestActiveUserDetail() = viewModelScope.launch {
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

    private fun updateUser(userImg: String, nickname: String, bio: String) = viewModelScope.launch {
        accountRepository.updateUser(
            targetUserId,
            userImg,
            nickname,
            bio
        )
            .collect {
                _eventResult.emit(Pair(Events.UPDATE_COMPLETE, null))
                requestActiveUserDetail()
            }
    }

    fun subscribeUser() = viewModelScope.launch {

    }

    fun checkNickname(nickname: String) = viewModelScope.launch {
        accountRepository.checkNickname(nickname).collect {
            val event = if (it.isSuccessful) Events.NICKNAME_VALID else Events.NICKNAME_INVALID

            _eventResult.emit(Pair(event, nickname))
        }
    }

    fun updateProfile(imageUri: Uri?, nickname: String, profileText: String) =
        viewModelScope.launch {
            if (imageUri != null) {
                val imageUrl = String.format(
                    application.applicationContext.getString(R.string.format_upload_file),
                    activeUserId, System.currentTimeMillis().toString()
                )
                val ref =
                    storageReference.child(application.applicationContext.getString(R.string.storage_path_profile) + imageUrl)
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

class HomeViewModelFactory(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository,
    private val application: Application,
    private val intent: Intent
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                userRepository,
                recipeRepository,
                postRepository,
                accountRepository,
                application,
                intent
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}