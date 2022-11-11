package com.android.source.network.employees.entity.update


import com.google.gson.annotations.SerializedName

data class UpdateEmployeeRequestEntity(
    @SerializedName("address")
    val address: String, // toshken shaxar olmazor tumani 12/1/46
    @SerializedName("first_name")
    val firstname: String, // Malik
    @SerializedName("job_id")
    val jobId: Int, // 1
    @SerializedName("last_name")
    val lastname: String, // jurayev
    @SerializedName("password")
    val password: String, // 123456
    @SerializedName("phone_number")
    val phoneNumber: String // 998945890505
)