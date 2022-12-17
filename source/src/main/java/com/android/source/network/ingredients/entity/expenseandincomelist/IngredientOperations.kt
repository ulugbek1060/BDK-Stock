package com.android.source.network.ingredients.entity.expenseandincomelist

import com.google.gson.annotations.SerializedName

data class IngredientOperations(
   @SerializedName("data") val operationsList: List<ExOrInIngredient>
)
