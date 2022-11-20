package com.android.source.network.clients.entity.createclients


import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("address")
    val address: String, // Toshkent viloyati , Toshkent shahri
    @SerializedName("created_at")
    val createdAt: String, // 2022-11-16T20:58:56.000000Z
    @SerializedName("id")
    val id: Long, // 3
    @SerializedName("name")
    val name: String, // Jirniy klient
    @SerializedName("phone_number")
    val phoneNumber: String, // 998978644454
    @SerializedName("total_sum")
    val totalSum: Any? = null, // null
    @SerializedName("updated_at")
    val updatedAt: String // 2022-11-16T20:58:56.000000Z
)