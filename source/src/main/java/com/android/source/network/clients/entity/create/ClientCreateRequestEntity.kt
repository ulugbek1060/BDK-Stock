package com.android.source.network.clients.entity.create


import com.google.gson.annotations.SerializedName

data class ClientCreateRequestEntity(
    @SerializedName("addres")
    val address: String, // Toshkent viloyati , Toshkent shahri
    @SerializedName("name")
    val name: String, // Jirniy klient
    @SerializedName("phone_number")
    val phoneNumber: String // 998978644454
)