package com.android.model.database.employees.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.model.repository.employees.entity.EmployeeEntity

@Entity(tableName = "employee")
data class EmployeeRoomEntity(
   @PrimaryKey val id: Long,
   val address: String,
   val firstname: String,
   val jobTitle: String,
   val lastname: String,
   val phoneNumber: String
) {
   fun toEmployeeEntity() = EmployeeEntity(
      id = id,
      firstname = firstname,
      lastname = lastname,
      address = address,
      jobTitle = jobTitle,
      phoneNumber = phoneNumber
   )
}
