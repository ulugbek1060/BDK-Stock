package com.android.source.network.employees.entity.get


import com.google.gson.annotations.SerializedName

data class Employee(
   @SerializedName("address")
   val address: String, // toshken shaxar olmazor tumani 12/1/46
   @SerializedName("first_name")
   val firstname: String, // Malik
   @SerializedName("id")
   val id: Long, // 3
   @SerializedName("job")
   val job: String, // Admin
   @SerializedName("last_name")
   val lastname: String, // jurayev
   @SerializedName("phone_number")
   val phoneNumber: String // 998945890507
)