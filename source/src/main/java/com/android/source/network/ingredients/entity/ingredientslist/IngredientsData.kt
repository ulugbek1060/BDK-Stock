package com.android.source.network.ingredients.entity.ingredientslist

import com.google.gson.annotations.SerializedName

data class IngredientsData(
   @SerializedName("data") val list: List<Ingredient>
)