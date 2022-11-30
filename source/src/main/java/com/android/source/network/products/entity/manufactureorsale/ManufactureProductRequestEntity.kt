package com.android.source.network.products.entity.manufactureorsale

import com.google.gson.annotations.SerializedName

data class ManufactureProductRequestEntity(
   @SerializedName("product_id") val productId: Long,
   @SerializedName("amount") val amount: String,
   @SerializedName("comment") val comment: String? = null
)
