package com.yhjoo.dochef

import com.yhjoo.dochef.data.repository.RecipeRepository

object RECIPE{
    object SEARCHBY {
        const val ALL = 0
        const val USERID = 1
        const val INGREDIENT = 2
        const val RECIPENAME = 3
        const val TAG = 4
    }
    object SORT {
        const val LATEST = "latest"
        const val POPULAR = "popular"
        const val RATING = "rating"
    }
    object THEME {
        const val POPULAR = 0
        const val TAG = 1
    }
}

