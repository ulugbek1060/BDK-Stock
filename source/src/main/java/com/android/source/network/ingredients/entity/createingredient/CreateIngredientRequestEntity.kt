package com.android.source.network.ingredients.entity.createingredient


import com.google.gson.annotations.SerializedName

data class CreateIngredientRequestEntity(
    @SerializedName("name")
    val name: String, // KOST
    @SerializedName("unit")
    val unit: String // kg
)