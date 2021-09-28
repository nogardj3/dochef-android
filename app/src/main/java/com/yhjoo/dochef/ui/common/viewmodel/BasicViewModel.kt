package com.yhjoo.dochef.ui.common.viewmodel

import androidx.core.text.parseAsHtml
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.data.repository.BasicRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class BasicViewModel(
    private val repository: BasicRepository
) : ViewModel() {
    val allNotices = MutableLiveData<ArrayList<ExpandableItem>>()
    val allFAQs = MutableLiveData<ArrayList<ExpandableItem>>()
    val tosText = MutableLiveData<CharSequence>()

    fun requestFAQs() {
        viewModelScope.launch {
            repository.getFAQs().collect {
                allFAQs.value = it.body()
            }
        }
    }

    fun requestNotices() {
        viewModelScope.launch {
            repository.getNotices().collect {
                allNotices.value = it.body()
            }
        }
    }

    fun requestTosText() {
        viewModelScope.launch {
            repository.getTOS().collect {
                tosText.value = it.body()!!["message"].asString.parseAsHtml()
            }
        }
    }
}

class BasicViewModelFactory(private val repository: BasicRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BasicViewModel::class.java)) {
            return BasicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}