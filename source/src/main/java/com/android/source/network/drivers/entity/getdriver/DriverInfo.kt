package com.android.source.network.drivers.entity.getdriver


import com.google.gson.annotations.SerializedName

data class DriverInfo(
    @SerializedName("automodel_id")
    val automodelId: Int, // 12
    @SerializedName("avto_number")
    val avtoNumber: String, // 022BEK
    @SerializedName("created_at")
    val createdAt: String, // 2022-11-15T13:06:40.000000Z
    @SerializedName("id")
    val id: Long, // 5
    @SerializedName("is_delete")
    val isDelete: String, // 0
    @SerializedName("name")
    val name: String, // Ulugbek Ulashev
    @SerializedName("phone_number")
    val phoneNumber: String, // 998903381060
    @SerializedName("updated_at")
    val updatedAt: String // 2022-11-15T13:08:16.000000Z
)