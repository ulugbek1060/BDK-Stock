package com.android.source.network.sales.entity.detailorder


import com.google.gson.annotations.SerializedName

data class OrderDetailResponseEntity(
   @SerializedName("data") val orderDetail: OrderDetail
)