package com.android.source.network.dashboard.entity.paychecklist

import com.google.gson.annotations.SerializedName

data class Paycheck(
   @SerializedName("id") val id: Long,
   @SerializedName("order_id") val orderId: Long?,
   @SerializedName("name") val name: String,
   @SerializedName("amount") val amount: String,
   @SerializedName("comment") val comment: String?,
   @SerializedName("pay_type") val payType: String?,
   @SerializedName("created_at") val createdAt: String?
)
