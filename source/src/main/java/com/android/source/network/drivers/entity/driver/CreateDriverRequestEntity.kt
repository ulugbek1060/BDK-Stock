package com.android.source.network.drivers.entity.driver


import com.google.gson.annotations.SerializedName

data class CreateDriverRequestEntity(
   @SerializedName("automodel_id")
   val autoModelId: Int, // id of vehicle
   @SerializedName("avto_number")
   val autoRegNumber: String, // A777AA
   @SerializedName("name")
   val driverName: String, // Jirniy driver
   @SerializedName("phone_number")
   val phoneNumber: String // 998978644454
)