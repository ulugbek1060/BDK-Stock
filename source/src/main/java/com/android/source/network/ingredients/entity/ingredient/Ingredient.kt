package com.android.source.network.ingredients.entity.ingredient


import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("id")
    val id: Int, // 3
    @SerializedName("name")
    val name: String, // KOST
    @SerializedName("unit")
    val unit: String // kg
)