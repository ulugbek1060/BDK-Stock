package com.android.source.network.ingredients.entity.ingredientslist

import com.google.gson.annotations.SerializedName

data class IngredientsListResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val data: IngredientsData
)