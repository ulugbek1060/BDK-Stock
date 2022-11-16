package com.android.source.network.clients.entity.update


import com.google.gson.annotations.SerializedName

data class ClientUpdateResponseEntity(
    @SerializedName("msg")
    val msg: String, // updated succesfuly
    @SerializedName("ok")
    val ok: Boolean // true
)