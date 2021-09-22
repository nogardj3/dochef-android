package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.model.UserDetail
import com.yhjoo.dochef.repository.FollowListRepository
import com.yhjoo.dochef.utilities.RetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val repository: FollowListRepository
) : ViewModel() {
    val activeUserDetail = MutableLiveData<UserDetail>()
    val allFollowLists = MutableLiveData<List<UserBrief>>()

    init{
        requestActiveUserDetail()
        requestFollowLists()
    }

    fun requestActiveUserDetail(){
        viewModelScope.launch {
            repository.getUserDetail().collect {
                activeUserDetail.value = it.body()
            }
        }
    }

    fun requestFollowLists() {
        viewModelScope.launch {
            repository.getFollowList().collect {
                allFollowLists.value = it.body()
            }
        }
    }

    fun subscribeUser(targetID: String){
        viewModelScope.launch {
            val result = repository.subscribeUser(targetID)
            if(result!!.isSuccessful){
                requestActiveUserDetail()
                requestFollowLists()
            }
        }
    }

    fun unsubscribeUser(targetID: String){
        viewModelScope.launch {
            val result = repository.unsubscribeUser(targetID)
            if(result!!.isSuccessful){
                requestActiveUserDetail()
                requestFollowLists()
            }
        }
    }
}

class FollowListViewModelFactory(private val repository: FollowListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowListViewModel::class.java)) {
            return FollowListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}