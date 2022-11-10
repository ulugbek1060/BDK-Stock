package com.android.model.repository.employees.entity

import com.android.model.database.employees.entity.EmployeeRoomEntity
import java.io.Serializable

data class EmployeeEntity(
   val address: String,
   val firstname: String,
   val id: Long,
   val jobTitle: String,
   val lastname: String,
   val phoneNumber: String
) : Serializable {

   fun toEmployeesRoomEntity() = EmployeeRoomEntity(
      address = address,
      firstname = firstname,
      id = id,
      jobTitle = jobTitle,
      lastname = lastname,
      phoneNumber = phoneNumber,
   )

}