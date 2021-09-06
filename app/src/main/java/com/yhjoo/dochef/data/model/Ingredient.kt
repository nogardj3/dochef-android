package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Ingredient(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: String
) : Serializable