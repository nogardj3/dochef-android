package com.yhjoo.dochef.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.utils.AuthUtil
import com.yhjoo.dochef.utils.DatastoreUtil
import com.yhjoo.dochef.utils.OtherUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val app: Application,
    private val repository: BasicRepository
) : ViewModel() {
    private var isLogin = AuthUtil.isLogIn(app.applicationContext)

    private var _eventResult = MutableSharedFlow<Events>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                FirebaseAnalytics.getInstance(app.applicationContext).apply {
                    logEvent(FirebaseAnalytics.Event.APP_OPEN) {
                        param(FirebaseAnalytics.Param.ITEM_ID, Constants.ANALYTICS.ID.START)
                        param(FirebaseAnalytics.Param.ITEM_NAME, Constants.ANALYTICS.NAME.START)
                    }
                }

                checkServerAlive()
            }
        }
    }

    private suspend fun checkServerAlive() {
        repository.checkAlive()
            .catch { exception ->
                exception.printStackTrace()

                App.isServerAlive = false
                App.activeUserId = "test"
                _eventResult.emit(Events.DEAD)
            }
            .collect {
                if (it.isSuccessful) {
                    App.isServerAlive = true

                    if (isLogin) {
                        App.activeUserId =
                            DatastoreUtil.getUserBrief(app.applicationContext).userID
                        _eventResult.emit(Events.ALIVE_WITH_LOGIN)
                    } else
                        _eventResult.emit(Events.ALIVE)
                }
            }
    }

    enum class Events {
        ALIVE_WITH_LOGIN, ALIVE, DEAD
    }
}

class SplashViewModelFactory(
    private val app: Application,
    private val repository: BasicRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}