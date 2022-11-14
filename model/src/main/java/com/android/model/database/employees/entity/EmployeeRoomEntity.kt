package com.android.model.database.employees.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.repository.jobs.entity.JobEntity

@Entity(
   tableName = "employee",
   indices = [
      Index(
         "phoneNumber", unique = true
      )
   ]
)
data class EmployeeRoomEntity(
   @PrimaryKey val id: Long,
   val address: String,
   val firstname: String,
   val job: EmployeeJobRoom,
   val lastname: String,
   val phoneNumber: String
) {

   fun toEmployeeEntity() = EmployeeEntity(
      id = id,
      firstname = firstname,
      lastname = lastname,
      address = address,
      job = JobEntity(
         id = job.id,
         name = job.name
      ),
      phoneNumber = phoneNumber
   )

}
