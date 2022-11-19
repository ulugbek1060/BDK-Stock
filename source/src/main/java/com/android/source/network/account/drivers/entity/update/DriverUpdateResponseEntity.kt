package com.android.source.network.account.drivers.entity.update


import com.google.gson.annotations.SerializedName

data class DriverUpdateResponseEntity(
    @SerializedName("msg")
    val msg: String, // updated succesfuly
    @SerializedName("ok")
    val ok: Boolean // true
)