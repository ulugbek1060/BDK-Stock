package com.android.source.network.employees.entity.registeremplyees


import com.google.gson.annotations.SerializedName

data class RegisterEmployeeResponseEntity(
    @SerializedName("message")
    val message: String, // user successfully registered
    @SerializedName("user")
    val user: RegisterEmployee
)