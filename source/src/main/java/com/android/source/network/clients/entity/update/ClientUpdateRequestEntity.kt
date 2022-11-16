package com.android.source.network.clients.entity.update


import com.google.gson.annotations.SerializedName

data class ClientUpdateRequestEntity(
    @SerializedName("addres")
    val address: String, // Toshkent viloyati , Toshkent shahri
    @SerializedName("id")
    val id: Long, // 3
    @SerializedName("name")
    val name: String, // Jonny A
    @SerializedName("phone_number")
    val phoneNumber: String // 998978644454
)