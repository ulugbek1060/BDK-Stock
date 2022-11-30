package com.android.source.network.products.entity.product

import com.google.gson.annotations.SerializedName


data class ManufacturedOrExportedProduct(
   @SerializedName("id") val id: Long,
   @SerializedName("name") val name: String,
   @SerializedName("amount") val amount: String,
   @SerializedName("unit") val unit: String? = null,
   @SerializedName("comment") val comment: String? = null,
   @SerializedName("status") val status: Int,
   @SerializedName("user") val creator: String,
   @SerializedName("user_id") val creatorId: Long,
   @SerializedName("created_at") val createdAt: String,
   @SerializedName("updated_at") val updatedAt: String
)