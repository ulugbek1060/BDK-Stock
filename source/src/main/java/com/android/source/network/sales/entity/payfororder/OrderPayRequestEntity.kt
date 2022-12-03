package com.android.source.network.sales.entity.payfororder

import com.google.gson.annotations.SerializedName

data class OrderPayRequestEntity(
   @SerializedName("order_id") val orderId: Long,
   @SerializedName("card") val card: String,
   @SerializedName("cash") val cash: String
)