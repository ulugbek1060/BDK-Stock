package com.android.model.repository.employees.entity

import com.android.model.database.employees.entity.EmployeeJobRoom
import com.android.model.database.employees.entity.EmployeeRoomEntity
import com.android.model.repository.jobs.entity.JobEntity
import java.io.Serializable

data class EmployeeEntity(
   val address: String,
   val firstname: String,
   val id: Long,
   val job: JobEntity,
   val lastname: String,
   val phoneNumber: String
) : Serializable {

   fun toEmployeesRoomEntity() = EmployeeRoomEntity(
      address = address,
      firstname = firstname,
      id = id,
      job = EmployeeJobRoom(
         id = job.id,
         name = job.name
      ),
      lastname = lastname,
      phoneNumber = phoneNumber,
   )

}