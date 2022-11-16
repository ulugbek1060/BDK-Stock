package com.android.source.network.clients.entity.clientlist


import com.google.gson.annotations.SerializedName

data class ClientListResponseEntity(
   @SerializedName("data")
   val clients: Clients,
   @SerializedName("ok")
   val ok: Boolean // true
)