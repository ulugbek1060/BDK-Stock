package com.android.source.network.dashboard.entity.paychecklist

import com.google.gson.annotations.SerializedName

data class PaycheckResponseEntity(
   @SerializedName("paycheck") val data: List<Paycheck>
)
