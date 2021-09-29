package com.yhjoo.dochef.ui.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.follow.FollowListActivity.UIMODE.FOLLOWER
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val repository: UserRepository
) : ViewModel() {
    val activeUserId = MutableLiveData<String>()
    val currentUserId = MutableLiveData<String>()

    val activeUserDetail = MutableLiveData<UserDetail>()
    val allFollowLists = MutableLiveData<List<UserBrief>>()

    fun requestActiveUserDetail(userId: String) {
        viewModelScope.launch {
            repository.getUserDetail(userId).collect {
                activeUserDetail.value = it.body()
            }
        }
    }

    fun requestFollowLists(uiMode: Int) {
        viewModelScope.launch {
            if (uiMode == FOLLOWER)
                repository.getFollowers(currentUserId.value!!).collect {
                    allFollowLists.value = it.body()
                }
            else
                repository.getFollowings(currentUserId.value!!).collect {
                    allFollowLists.value = it.body()
                }
        }
    }

    fun subscribeUser(activeUserId: String, targetID: String) {
        viewModelScope.launch {
            repository.subscribeUser(activeUserId, targetID).collect {
                if(it.isSuccessful){
                    requestActiveUserDetail(activeUserId)
                }
            }
        }
    }

    fun unsubscribeUser(activeUuserId: String, targetID: String) {
        viewModelScope.launch {
            repository.unsubscribeUser(activeUuserId, targetID).collect {
                if(it.isSuccessful){
                    requestActiveUserDetail(activeUuserId)
                }
            }
        }
    }
}

class FollowListViewModelFactory(
    private val repository: UserRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowListViewModel::class.java)) {
            return FollowListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}