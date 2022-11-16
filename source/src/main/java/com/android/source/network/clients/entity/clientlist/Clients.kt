package com.android.source.network.clients.entity.clientlist


import com.android.source.network.clients.entity.create.Client
import com.google.gson.annotations.SerializedName

data class Clients(
   @SerializedName("data")
   val clients: List<Client>
)