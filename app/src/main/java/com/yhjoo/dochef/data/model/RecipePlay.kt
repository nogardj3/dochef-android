package com.yhjoo.dochef.data.model

import java.io.Serializable

data class RecipePlay(
    val recipeImg: Int,
    val title: String,
    val ingredients: Array<String>,
    val explain: String,
    val dateTime: Int,
    val tags: Array<String>
) : Serializable