package com.yhjoo.dochef.ui.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import com.yhjoo.dochef.utils.OtherUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    private val application: Application,
    intent: Intent,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
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
    private var _updateComplete = MutableLiveData<Boolean>()
    private var _nicknameValid = MutableLiveData<Pair<Boolean, String>>()

    val userDetail: LiveData<UserDetail>
        get() = _userDetail
    val allRecipes: LiveData<List<Recipe>>
        get() = _allRecipes
    val allPosts: LiveData<List<Post>>
        get() = _allPosts
    val updateComplete: LiveData<Boolean>
        get() = _updateComplete
    val nicknameValid: LiveData<Pair<Boolean, String>>
        get() = _nicknameValid

    init {
        requestActiveUserDetail()
        requestRecipeList()
        requestPostListById()
    }

    fun requestActiveUserDetail() {
        viewModelScope.launch {
            userRepository.getUserDetail(targetUserId).collect {
                _userDetail.value = it.body()
            }
        }
    }

    fun requestRecipeList() {
        viewModelScope.launch {
            recipeRepository.getRecipeList(
                Constants.RECIPE.SEARCHBY.USERID,
                Constants.RECIPE.SORT.LATEST,
                targetUserId
            ).collect {
                _allRecipes.value = it.body()
            }
        }
    }

    fun requestPostListById() {
        viewModelScope.launch {
            postRepository.getPostListByUserId(targetUserId).collect {
                _allPosts.value = it.body()
            }
        }
    }

    fun updateUser(userImg: String, nickname: String, bio: String) {
        viewModelScope.launch {
            accountRepository.updateUser(
                targetUserId,
                userImg,
                nickname,
                bio
            )
                .collect {
                    _updateComplete.value = true
                }
        }
    }

    fun checkNickname(nickname: String) {
        viewModelScope.launch {
            accountRepository.checkNickname(nickname).collect {
                OtherUtil.log(it.body().toString())
                _nicknameValid.value = Pair(it.isSuccessful, nickname)
            }
        }
    }

    fun updateProfile(imageUri: Uri?, nickname: String, profileText: String) {
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
}

class HomeViewModelFactory(
    private val application: Application,
    private val intent: Intent,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                application,
                intent,
                userRepository,
                recipeRepository,
                postRepository,
                accountRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}