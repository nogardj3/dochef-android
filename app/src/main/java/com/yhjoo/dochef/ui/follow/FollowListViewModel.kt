package com.yhjoo.dochef.ui.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val repository: UserRepository,
    private val uiMode: Int
) : ViewModel() {
    companion object {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }

    val activeUserDetail = MutableLiveData<UserDetail>()
    val allFollowLists = MutableLiveData<List<UserBrief>>()

    fun requestActiveUserDetail(userId: String) {
        viewModelScope.launch {
            repository.getUserDetail(userId).collect {
                activeUserDetail.value = it.body()
            }
        }
    }

    fun requestFollowLists(userId: String) {
        viewModelScope.launch {
            if (uiMode == FOLLOWER)
                repository.getFollowers(userId).collect {
                    allFollowLists.value = it.body()
                }
            else
                repository.getFollowings(userId).collect {
                    allFollowLists.value = it.body()
                }
        }
    }

    fun subscribeUser(userId: String, targetID: String) {
        viewModelScope.launch {
            val result = repository.subscribeUser(userId, targetID)
            if (result!!.isSuccessful) {
                requestActiveUserDetail(userId)
                requestFollowLists(userId)
            }
        }
    }

    fun unsubscribeUser(userId: String, targetID: String) {
        viewModelScope.launch {
            val result = repository.unsubscribeUser(userId, targetID)
            if (result!!.isSuccessful) {
                requestActiveUserDetail(userId)
                requestFollowLists(userId)
            }
        }
    }
}

class FollowListViewModelFactory(
    private val repository: UserRepository,
    private val uiMode: Int
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowListViewModel::class.java)) {
            return FollowListViewModel(
                repository,
                uiMode
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}