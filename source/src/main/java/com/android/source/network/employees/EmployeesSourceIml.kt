package com.android.source.network.employees

import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.employees.entity.register.RegisterEmployeeRequestEntity
import com.android.source.network.employees.entity.update.UpdateEmployeeRequestEntity
import javax.inject.Inject

class EmployeesSourceIml @Inject constructor(
   private val employeesApi: EmployeesApi
) : BaseNetworkSource(), EmployeesSource {

   private val TAG = this.javaClass.simpleName

   override suspend fun registerEmployee(
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      jobId: Int
   ): String = wrapNetworkException {
      val body = RegisterEmployeeRequestEntity(
         firstname = firstname,
         lastname = lastname,
         phoneNumber = phoneNumber,
         address = address,
         jobId = jobId
      )
      employeesApi.registerEmployee(body).message
   }

   override suspend fun getEmployees(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<EmployeeEntity> = wrapNetworkException {
      employeesApi.getEmployees(
         query = query,
         pageIndex = pageIndex,
         pageSize = pageSize
      ).data.map { employee ->
         EmployeeEntity(
            address = employee.address,
            firstname = employee.firstname,
            id = employee.id,
            jobTitle = employee.job,
            lastname = employee.lastname,
            phoneNumber = employee.phoneNumber
         )
      }
   }

   override suspend fun updateEmployee(
      id: Long,
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      password: String,
      job: Int
   ): String = wrapNetworkException {
      val body = UpdateEmployeeRequestEntity(
         firstname = firstname,
         lastname = lastname,
         phoneNumber = phoneNumber,
         address = address,
         password = password,
         jobId = job
      )
      employeesApi.updateEmployee(id, body).message
   }

}