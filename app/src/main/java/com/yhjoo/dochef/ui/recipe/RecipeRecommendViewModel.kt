package com.yhjoo.dochef.ui.recipe

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeRecommendViewModel(
    private val application: Application,
    intent: Intent,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    val activeUserId: String by lazy {
        DatastoreUtil.getUserBrief(application.applicationContext).userID
    }
    val tagName = intent.getStringExtra("tag")!!

    private var _allRecipeList = MutableLiveData<List<Recipe>>()
    val allRecipeList: LiveData<List<Recipe>>
        get() = _allRecipeList

    init {
        requestRecipeList(
            Constants.RECIPE.SEARCHBY.TAG,
            Constants.RECIPE.SORT.POPULAR,
            tagName
        )
    }

    fun requestRecipeList(searchby: Int, sort: String, searchValue: String?) =
        viewModelScope.launch {
            recipeRepository.getRecipeList(searchby, sort, searchValue).collect {
                _allRecipeList.postValue(it.body())
            }
        }
}

class RecipeRecommendViewModelFactory(
    private val application: Application,
    private val intent: Intent,
    private val repository: RecipeRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeRecommendViewModel::class.java)) {
            return RecipeRecommendViewModel(application, intent, repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}