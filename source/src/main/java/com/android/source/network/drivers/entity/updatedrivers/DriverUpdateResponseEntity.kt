package com.android.source.network.drivers.entity.updatedrivers


import com.google.gson.annotations.SerializedName

data class DriverUpdateResponseEntity(
    @SerializedName("msg")
    val msg: String, // updated succesfuly
    @SerializedName("ok")
    val ok: Boolean // true
)