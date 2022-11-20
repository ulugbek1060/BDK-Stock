package com.android.source.network.clients.entity.createclients


import com.google.gson.annotations.SerializedName

data class ClientCreateResponseEntity(
   @SerializedName("data")
   val client: Client,
   @SerializedName("ok")
   val ok: Boolean // true
)