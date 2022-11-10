package com.android.source.network.employees.entity

import com.google.gson.annotations.SerializedName

data class Employee(
   @SerializedName("address") val address: String,
   @SerializedName("first_name") val firstname: String,
   @SerializedName("id") val id: Long,
   @SerializedName("job") val jobTitle: String,
   @SerializedName("last_name") val lastname: String,
   @SerializedName("phone_number") val phoneNumber: String
)