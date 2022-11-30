package com.android.source.network.products.entity.updateproduct

import com.google.gson.annotations.SerializedName

data class UpdatedIngredient(
   @SerializedName("material_id") val ingredientId: Long,
   @SerializedName("name") val name: String,
   @SerializedName("amount") val amount: String,
   @SerializedName("unit") val unit: String
)
