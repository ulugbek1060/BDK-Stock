package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class OrderCreateResponseEntity(
   @SerializedName("data") val data: CreateOrderData
)