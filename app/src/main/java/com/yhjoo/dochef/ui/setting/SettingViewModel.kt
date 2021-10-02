package com.yhjoo.dochef.ui.setting

import androidx.core.text.parseAsHtml
import androidx.lifecycle.*
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.data.repository.BasicRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class SettingViewModel(
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

    fun requestFAQs() {
        viewModelScope.launch {
            repository.getFAQs().collect {
                _allFAQs.value = it.body()
            }
        }
    }

    fun requestNotices() {
        viewModelScope.launch {
            repository.getNotices().collect {
                _allNotices.value = it.body()
            }
        }
    }

    fun requestTosText() {
        viewModelScope.launch {
            repository.getTOS().collect {
                _tosText.value = it.body()!!["message"].asString.parseAsHtml()
            }
        }
    }
}

class SettingViewModelFactory(private val repository: BasicRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}