package com.android.source.network.drivers.entity.driverlist

import com.android.source.network.drivers.entity.createdrivers.Driver
import com.google.gson.annotations.SerializedName

data class DriversData(
   @SerializedName("data")
   val driverList: List<Driver>,
)