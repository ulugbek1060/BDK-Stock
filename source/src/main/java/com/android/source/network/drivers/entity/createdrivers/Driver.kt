package com.android.source.network.drivers.entity.createdrivers


import com.android.source.network.drivers.entity.automodel.VehicleModel
import com.google.gson.annotations.SerializedName

data class Driver(
   @SerializedName("avto_number")
    val avtoNumber: String, // 022BEK
   @SerializedName("id")
    val id: Long, // 5
   @SerializedName("model")
    val model: VehicleModel,
   @SerializedName("name")
    val name: String, // Ulugbek Ulashev
   @SerializedName("phone_number")
    val phoneNumber: String // 998911641060
)