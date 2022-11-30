package com.android.source.network.products.entity.createproduct

import com.android.source.network.products.entity.product.Product
import com.google.gson.annotations.SerializedName

data class ProductCreateResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val product: Product
)