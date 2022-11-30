package com.android.source.network.products.entity.updateproduct

import com.android.source.network.products.entity.product.Product
import com.google.gson.annotations.SerializedName

data class ProductUpdateResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val updatedProduct: Product
)