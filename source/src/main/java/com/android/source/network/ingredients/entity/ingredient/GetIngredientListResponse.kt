package com.android.source.network.ingredients.entity.ingredient

import com.google.gson.annotations.SerializedName

data class GetIngredientListResponse(
   @SerializedName("data") val list: List<Ingredient>
)