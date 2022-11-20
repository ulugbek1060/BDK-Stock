package com.android.source.network.employees.entity.updateemployees


import com.google.gson.annotations.SerializedName

data class UpdateEmployeeResponseEntity(
   @SerializedName("message")
   val message: String, // user successfully updated
   @SerializedName("user")
   val employee: UpdateEmployee
)