package com.yhjoo.dochef.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NotificationViewModel (application: Application) : AndroidViewModel(application){
    var type = ObservableInt()
    var intent =  ObservableField<String>()
    var intentData =  ObservableField<String>()
    var contents =  ObservableField<String>()
    var img =  ObservableField<String>()
    var date_time =  ObservableLong()
    var isRead =  ObservableInt()

    init {
        viewModelScope.launch {
            // Coroutine that will be canceled when the ViewModel is cleared.
        }
    }
}