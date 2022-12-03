package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class ProductForCreateOrder(
   @SerializedName("id") val id: Long,
   @SerializedName("amount") val amount: String
)