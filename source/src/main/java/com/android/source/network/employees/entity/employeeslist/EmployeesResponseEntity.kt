package com.android.source.network.employees.entity.employeeslist

import com.google.gson.annotations.SerializedName

data class EmployeesResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val data: List<Employee>
)