package com.yhjoo.dochef.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.entity.NotificationEntity
import com.yhjoo.dochef.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
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