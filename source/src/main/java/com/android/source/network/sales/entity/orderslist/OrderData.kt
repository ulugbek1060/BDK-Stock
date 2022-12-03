package com.android.source.network.sales.entity.orderslist


import com.google.gson.annotations.SerializedName

data class OrderData(
   @SerializedName("client") val client: String,
   @SerializedName("data") val createdAt: String,
   @SerializedName("id") val id: Long,
   @SerializedName("name") val name: String,
   @SerializedName("status") val status: Int,
   @SerializedName("summa") val summa: String
)