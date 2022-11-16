package com.android.source.network.clients.entity.getclient


import com.android.source.network.clients.entity.create.Client
import com.google.gson.annotations.SerializedName

data class ClientByIdResponseEntity(
   @SerializedName("data")
   val client: Client,
   @SerializedName("ok")
   val ok: Boolean // true
)