package com.android.source.network.ingredients.entity.addexpenseorincome

import com.android.source.network.ingredients.entity.expenseandincomelist.ExOrInIngredient
import com.google.gson.annotations.SerializedName

data class AddExOrInIngredientsResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val exOrInIngredients: ExOrInIngredient
)