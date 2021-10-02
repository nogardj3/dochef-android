package com.yhjoo.dochef.ui.follow

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.follow.FollowListActivity.UIMODE.FOLLOWER
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val application: Application,
    private val repository: UserRepository,
    intent: Intent,
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    private var currentUserId: String = intent.getStringExtra("userID").toString()

    private var _activeUserDetail = MutableLiveData<UserDetail>()
    private var _allFollowLists = MutableLiveData<List<UserBrief>>()

    val activeUserDetail: LiveData<UserDetail>
        get() = _activeUserDetail
    val allFollowLists: LiveData<List<UserBrief>>
        get() = _allFollowLists

    init{
        requestActiveUserDetail()
    }

    fun requestActiveUserDetail() {
        viewModelScope.launch {
            repository.getUserDetail(activeUserId).collect {
                _activeUserDetail.value = it.body()
            }
        }
    }

    fun requestFollowLists(uiMode: Int) {
        viewModelScope.launch {
            if (uiMode == FOLLOWER)
                repository.getFollowers(currentUserId).collect {
                    _allFollowLists.value = it.body()
                }
            else
                repository.getFollowings(currentUserId).collect {
                    _allFollowLists.value = it.body()
                }
        }
    }

    fun subscribeUser(targetID: String) {
        viewModelScope.launch {
            repository.subscribeUser(activeUserId, targetID).collect {
                if (it.isSuccessful) {
                    requestActiveUserDetail()
                }
            }
        }
    }

    fun unsubscribeUser(targetID: String) {
        viewModelScope.launch {
            repository.unsubscribeUser(activeUserId, targetID).collect {
                if (it.isSuccessful) {
                    requestActiveUserDetail()
                }
            }
        }
    }
}

class FollowListViewModelFactory(
    private val application: Application,
    private val repository: UserRepository,
    private val intent: Intent,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowListViewModel::class.java)) {
            return FollowListViewModel(application, repository, intent) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}