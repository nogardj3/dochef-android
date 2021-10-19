package com.yhjoo.dochef.ui.notification

import androidx.lifecycle.*
import com.yhjoo.dochef.data.entity.NotificationEntity
import com.yhjoo.dochef.data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val notificationRepository: NotificationRepository) :
    ViewModel() {
    val allnotifications: LiveData<List<NotificationEntity>> =
        notificationRepository.notifications.asLiveData()

    private var _eventResult = MutableSharedFlow<Pair<Any, NotificationEntity>>()
    val eventResult = _eventResult.asSharedFlow()

    fun setRead(notification: NotificationEntity) = viewModelScope.launch(Dispatchers.IO) {
        notificationRepository.setRead(notification.id!!)
        _eventResult.emit(Pair(Events.ISCLICKED, notification))
    }

    enum class Events {
        ISCLICKED
    }
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