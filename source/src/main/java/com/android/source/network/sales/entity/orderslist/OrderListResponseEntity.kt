package com.android.source.network.sales.entity.orderslist


import com.google.gson.annotations.SerializedName

data class OrderListResponseEntity(
   @SerializedName("ok") val ok: Boolean,
   @SerializedName("data") val orders: OrdersData
)