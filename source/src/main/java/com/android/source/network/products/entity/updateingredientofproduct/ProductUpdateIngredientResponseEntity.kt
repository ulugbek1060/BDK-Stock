package com.android.source.network.products.entity.updateingredientofproduct

import com.android.source.network.products.entity.updateproduct.UpdatedIngredient
import com.google.gson.annotations.SerializedName

data class ProductUpdateIngredientResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val updatedIngredientList: List<UpdatedIngredient>
)