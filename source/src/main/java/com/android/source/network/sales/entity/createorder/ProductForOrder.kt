package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class ProductForOrder(
   @SerializedName("id") val id: Long,
   @SerializedName("name") val name: String,
   @SerializedName("unit") val unit: String,
   @SerializedName("amount") val amount: String,
   @SerializedName("price") val price: String,
   @SerializedName("summa") val summa: String
)
