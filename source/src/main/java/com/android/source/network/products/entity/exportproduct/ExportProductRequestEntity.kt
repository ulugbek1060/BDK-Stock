package com.android.source.network.products.entity.exportproduct

import com.google.gson.annotations.SerializedName

data class ExportProductRequestEntity(
   @SerializedName("product_id") val productId: Long,
   @SerializedName("amount") val amount: String,
   @SerializedName("comment") val comment: String
)
