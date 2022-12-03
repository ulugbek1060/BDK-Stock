package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class OrderCreateRequestEntity(
   @SerializedName("client_id") val clientId: Long,
   @SerializedName("driver_id") val driverId: Long,
   @SerializedName("product") val products: List<ProductForCreateOrder>
)