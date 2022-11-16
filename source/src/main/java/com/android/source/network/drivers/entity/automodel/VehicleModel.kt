package com.android.source.network.drivers.entity.automodel


import com.google.gson.annotations.SerializedName

data class VehicleModel(
    @SerializedName("id")
    val id: Int, // 13
    @SerializedName("name")
    val name: String // Mercedes benz
)