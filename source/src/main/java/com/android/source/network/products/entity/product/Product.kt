package com.android.source.network.products.entity.product

import com.google.gson.annotations.SerializedName

data class Product(
   @SerializedName("id") val id: Long,
   @SerializedName("name") val name: String,
   @SerializedName("unit") val unit: String,
   @SerializedName("amount") val amount: String,
   @SerializedName("price") val price: String,
   @SerializedName("summa") val summa: String,
   @SerializedName("is_delete") val isDelete: String,
   @SerializedName("created_at") val createdAt: String,
   @SerializedName("updated_at") val updatedAt: String
)