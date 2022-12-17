package com.android.source.network.sales.entity.orderslist

import com.google.gson.annotations.SerializedName

data class OrdersData(
   @SerializedName("data") val orderList: List<OrderData>
)