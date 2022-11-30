package com.android.source.network.products.entity.product

import com.google.gson.annotations.SerializedName

data class IngredientOfProduct(
   @SerializedName("id") val id: Long,
   @SerializedName("amount") val amount: String
)
