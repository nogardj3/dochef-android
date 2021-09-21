package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
    var type = MutableLiveData<Int>()
    var intent = MutableLiveData<String>()
    var intentData = MutableLiveData<String>()
    var contents = MutableLiveData<String>()
    var img = MutableLiveData<String>()
    var date_time = MutableLiveData<Long>()
    var isRead = MutableLiveData<Int>()

    fun getRecentList(dateTime: Long) {
        viewModelScope.launch {
            repository.getRecentList(dateTime)
        }
    }
}

class NotificationViewModelFactory(private val repository: NotificationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotificationViewModel::class.java)){
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}