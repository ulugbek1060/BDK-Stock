package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class CreateOrderData(
   @SerializedName("id") val id: Long,
   @SerializedName("name") val orderIdentifier: String,
   @SerializedName("status") val status: Int,
   @SerializedName("summa") val summa: String,
   @SerializedName("paid") val paid: String,
   @SerializedName("debit") val debit: String,
   @SerializedName("created_at") val createdAt: String,
   @SerializedName("client") val client: OrderForClient,
   @SerializedName("driver") val driver: DriverForOrder,
   @SerializedName("product") val products: List<ProductForOrder>
)