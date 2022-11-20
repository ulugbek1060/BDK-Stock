package com.android.source.network.drivers.entity.updatedrivers


import com.google.gson.annotations.SerializedName

data class DriverUpdateRequestEntity(
    @SerializedName("automodel_id")
    val automodelId: Int, // 12
    @SerializedName("avto_number")
    val avtoNumber: String, // 022BEK
    @SerializedName("id")
    val id: Long, // 5
    @SerializedName("name")
    val name: String, // Ulugbek Ulashev
    @SerializedName("phone_number")
    val phoneNumber: String // 998903381060
)