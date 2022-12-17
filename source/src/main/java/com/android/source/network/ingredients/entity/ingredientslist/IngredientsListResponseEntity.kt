package com.android.source.network.ingredients.entity.ingredientslist

import com.google.gson.annotations.SerializedName

data class IngredientsListResponseEntity(
//   @SerializedName("ok") val ok: Boolean,
   @SerializedName("data") val data: IngredientsData
)