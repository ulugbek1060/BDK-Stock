package com.android.source.network.account.drivers.entity.automodel

import com.google.gson.annotations.SerializedName

data class VehicleModelResponseEntity(
   @SerializedName("data")
   val modelsList: List<VehicleModel>
)

