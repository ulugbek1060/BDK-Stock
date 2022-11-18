package com.android.source.network.employees.entity.searchby

import com.android.source.network.employees.entity.get.Employee
import com.google.gson.annotations.SerializedName

data class EmployeeSearchByResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("user") val data: List<Employee>
)