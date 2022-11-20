package com.android.source.network.employees.entity.searchbyemployees

import com.android.source.network.employees.entity.employeeslist.Employee
import com.google.gson.annotations.SerializedName

data class EmployeeSearchByResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("user") val data: List<Employee>
)