package com.yhjoo.dochef.model

import java.io.Serializable

data class RecipePlay(
    val recipeImg: Int,
    val title: String,
    val ingredients: Array<String>,
    val explain: String,
    val dateTime: Int,
    val tags: Array<String>
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipePlay

        if (recipeImg != other.recipeImg) return false
        if (title != other.title) return false
        if (!ingredients.contentEquals(other.ingredients)) return false
        if (explain != other.explain) return false
        if (dateTime != other.dateTime) return false
        if (!tags.contentEquals(other.tags)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipeImg
        result = 31 * result + title.hashCode()
        result = 31 * result + ingredients.contentHashCode()
        result = 31 * result + explain.hashCode()
        result = 31 * result + dateTime
        result = 31 * result + tags.contentHashCode()
        return result
    }
}