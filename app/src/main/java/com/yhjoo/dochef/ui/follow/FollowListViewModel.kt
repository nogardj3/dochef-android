package com.yhjoo.dochef.ui.follow

import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.follow.FollowListActivity.Companion.FOLLOWER
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val repository: UserRepository,
    intent: Intent,
) : ViewModel() {
    val activeUserId = App.activeUserId

    private val currentUiMode = intent.getIntExtra("mode", FOLLOWER)
    private val currentUserId = intent.getStringExtra(Constants.INTENTNAME.USER_ID)
    val title = if (currentUiMode == FOLLOWER) "Follower" else "Following"

    private var _activeUserDetail = MutableLiveData<UserDetail>()
    val activeUserDetail: LiveData<UserDetail>
        get() = _activeUserDetail

    private var _allFollowLists = MutableLiveData<List<UserBrief>>()
    val allFollowLists: LiveData<List<UserBrief>>
        get() = _allFollowLists

    init {
        requestActiveUserDetail()
        requestFollowLists()
    }

    private fun requestActiveUserDetail() = viewModelScope.launch {
        repository.getUserDetail(activeUserId).collect {
            _activeUserDetail.value = it.body()
        }
    }

    private fun requestFollowLists() = viewModelScope.launch {
        if (currentUiMode == FOLLOWER)
            repository.getFollowers(currentUserId!!).collect {
                _allFollowLists.value = it.body()
            }
        else
            repository.getFollowings(currentUserId!!).collect {
                _allFollowLists.value = it.body()
            }
    }

    fun subscribeUser(targetID: String) = viewModelScope.launch {
        repository.subscribeUser(activeUserId, targetID).collect {
            if (it.isSuccessful) {
                requestActiveUserDetail()
                requestFollowLists()
            }
        }
    }

    fun unsubscribeUser(targetID: String) = viewModelScope.launch {
        repository.unsubscribeUser(activeUserId, targetID).collect {
            if (it.isSuccessful) {
                requestActiveUserDetail()
                requestFollowLists()
            }
        }
    }
}

class FollowListViewModelFactory(
    private val repository: UserRepository,
    private val intent: Intent,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowListViewModel::class.java)) {
            return FollowListViewModel(repository, intent) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}