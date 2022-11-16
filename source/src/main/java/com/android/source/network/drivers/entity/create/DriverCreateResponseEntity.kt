package com.android.source.network.drivers.entity.create


import com.google.gson.annotations.SerializedName

data class DriverCreateResponseEntity(
    @SerializedName("data")
   val driver: Driver,
    @SerializedName("ok")
   val ok: Boolean // true
)