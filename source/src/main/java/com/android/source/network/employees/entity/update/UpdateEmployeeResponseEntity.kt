package com.android.source.network.employees.entity.update


import com.android.source.network.employees.entity.get.Employee
import com.google.gson.annotations.SerializedName

data class UpdateEmployeeResponseEntity(
   @SerializedName("message")
   val message: String, // user successfully updated
   @SerializedName("user")
   val employee: UpdateEmployee
)