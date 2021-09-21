package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.*
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.repository.NotificationRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
    val allnotifications: LiveData<List<NotificationEntity>> = repository.notifications.asLiveData()

    var type = MutableLiveData<Int>()
    var intent = MutableLiveData<String>()
    var intentData = MutableLiveData<String>()
    var contents = MutableLiveData<String>()
    var img = MutableLiveData<String>()
    var date_time = MutableLiveData<Long>()
    var isRead = MutableLiveData<Int>()
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