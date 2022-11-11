package com.android.source.network.employees.entity.register


import com.google.gson.annotations.SerializedName

data class RegisterEmployee(
    @SerializedName("address")
    val address: String, // toshken shaxar olmazor tumani 12/1/46
    @SerializedName("first_name")
    val firstName: String, // Axmadjon
    @SerializedName("id")
    val id:Long, // 4
    @SerializedName("job")
    val job: String, // Admin
    @SerializedName("last_name")
    val lastName: String, // Jalolov
    @SerializedName("phone_number")
    val phoneNumber: String // 998945890502
)