package com.android.source.network.products.entity.updateingredientofproduct

import com.android.source.network.products.entity.product.IngredientOfProduct
import com.google.gson.annotations.SerializedName

data class ProductUpdateIngredientRequestEntity(
   @SerializedName("product_id") val productId: Long,
   @SerializedName("material") val newIngredientList: List<IngredientOfProduct>
)