package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.*
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.repository.FollowListRepository
import com.yhjoo.dochef.ui.activities.FollowListActivity
import com.yhjoo.dochef.utilities.RetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val mode:Int,
    private val repository: FollowListRepository) : ViewModel() {
    val allFollowLists = MutableLiveData<List<UserBrief>>()

    fun requestFollowLists(targetId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if(mode == FollowListActivity.UIMODE.FOLLOWER)
                repository.getFollowers(targetId).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { allFollowLists.postValue(response.body()!!) }
                    } else
                        RetrofitBuilder.defaultErrorHandler(response)
                }
            else{
                repository.getFollowings(targetId)?.let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { allFollowLists.postValue(it.items) }
                    }
                    else
                        RetrofitBuilder.defaultErrorHandler(it)
                }
            }
        }
    }

}

class FollowListViewModelFactory(private val repository: FollowListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return FollowListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}