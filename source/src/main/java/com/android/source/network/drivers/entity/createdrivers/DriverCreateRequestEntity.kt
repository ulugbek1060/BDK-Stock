package com.android.source.network.drivers.entity.createdrivers


import com.google.gson.annotations.SerializedName

data class DriverCreateRequestEntity(
    @SerializedName("automodel_id")
    val automodelId: Int, // 13
    @SerializedName("avto_number")
    val avtoNumber: String, // 022BEK
    @SerializedName("name")
    val name: String, // Ulugbek Ulashev
    @SerializedName("phone_number")
    val phoneNumber: String // 998911641060
)