package com.android.source.network.account.drivers.entity.driverlist


import com.android.source.network.account.drivers.entity.create.Driver
import com.google.gson.annotations.SerializedName

data class DriversListResponseEntity(
   @SerializedName("data")
    val driverList: List<Driver>,
   @SerializedName("message")
    val message: String // success
)