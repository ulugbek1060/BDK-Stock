package com.android.source.network.drivers.entity.driverlist


import com.android.source.network.drivers.entity.createdrivers.Driver
import com.google.gson.annotations.SerializedName

data class DriversListResponseEntity(
   @SerializedName("data")
   val drivers: DriversData,
   @SerializedName("message")
   val message: String // success
)