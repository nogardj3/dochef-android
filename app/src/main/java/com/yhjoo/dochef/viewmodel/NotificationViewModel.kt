package com.yhjoo.dochef.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.repository.NotificationRepository

class NotificationViewModel(repository: NotificationRepository) : ViewModel() {
    val allnotifications: LiveData<List<NotificationEntity>> = repository.notifications.asLiveData()
}

class NotificationViewModelFactory(private val repository: NotificationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}