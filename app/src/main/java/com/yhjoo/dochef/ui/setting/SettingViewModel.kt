package com.yhjoo.dochef.ui.setting

import androidx.core.text.parseAsHtml
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.data.repository.BasicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: BasicRepository
) : ViewModel() {
    private var _allNotices = MutableLiveData<ArrayList<ExpandableItem>>()
    private var _allFAQs = MutableLiveData<ArrayList<ExpandableItem>>()
    private var _tosText = MutableLiveData<CharSequence>()

    val allNotices: LiveData<ArrayList<ExpandableItem>>
        get() = _allNotices
    val allFAQs: LiveData<ArrayList<ExpandableItem>>
        get() = _allFAQs
    val tosText: LiveData<CharSequence>
        get() = _tosText

    init {
        viewModelScope.launch(Dispatchers.IO) {
            requestFAQs()
            requestNotices()
            requestTosText()
        }
    }

    private suspend fun requestFAQs() {
        repository.getFAQs().collect {
            _allFAQs.postValue(it.body())
        }
    }

    private suspend fun requestNotices() {
        repository.getNotices().collect {
            _allNotices.postValue(it.body())
        }
    }

    private suspend fun requestTosText() {
        repository.getTOS().collect {
            _tosText.postValue(it.body()!!["message"].asString.parseAsHtml())
        }
    }
}