package com.android.source.network.drivers.entity.getdriver


import com.google.gson.annotations.SerializedName

data class DriverInfoResponseEntity(
   @SerializedName("data")
   val driverInfo: DriverInfo,
   @SerializedName("ok")
   val ok: Boolean // true
)