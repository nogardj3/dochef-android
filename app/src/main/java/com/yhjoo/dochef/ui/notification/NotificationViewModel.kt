package com.yhjoo.dochef.ui.notification

import androidx.lifecycle.*
import com.yhjoo.dochef.data.entity.NotificationEntity
import com.yhjoo.dochef.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {
    val allnotifications: LiveData<List<NotificationEntity>>
        = notificationRepository.notifications.asLiveData()

    fun setRead(notificationId: Long){
        viewModelScope.launch {
            notificationRepository.setRead(notificationId)
        }
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

//class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {
//    private var _allnotifications = MutableLiveData<List<NotificationEntity>>()
//    val allnotifications: LiveData<List<NotificationEntity>>
//        get() = _allnotifications
//
//
//    init{
//        getData()
//    }
//
//    private fun getData(){
//        viewModelScope.launch {
//            notificationRepository.getData().collect {
//                _allnotifications.value
//            }
//        }
//    }
//
//    fun setRead(notificationId: Long){
//        viewModelScope.launch {
//            notificationRepository.setRead(notificationId)
//        }
//    }
//}
//
//class NotificationViewModelFactory(private val repository: NotificationRepository) :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
//            return NotificationViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown View Model class")
//    }
//}