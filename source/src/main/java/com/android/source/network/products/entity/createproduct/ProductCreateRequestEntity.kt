package com.android.source.network.products.entity.createproduct

import com.android.source.network.products.entity.product.IngredientOfProduct
import com.google.gson.annotations.SerializedName

data class ProductCreateRequestEntity(
   @SerializedName("name") val name: String,
   @SerializedName("unit") val unit: String,
   @SerializedName("price") val price: String,
   @SerializedName("material") val materials: List<IngredientOfProduct>
)