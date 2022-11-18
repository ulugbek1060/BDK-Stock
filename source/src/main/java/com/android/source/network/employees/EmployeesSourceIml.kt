package com.android.source.network.employees

import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.repository.jobs.entity.JobEntity
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
   ): EmployeeEntity = wrapNetworkException {
      val body = RegisterEmployeeRequestEntity(
         firstname = firstname,
         lastname = lastname,
         phoneNumber = phoneNumber,
         address = address,
         jobId = jobId
      )
      val response = employeesApi.registerEmployee(body).user
      EmployeeEntity(
         id = response.id,
         firstname = response.firstname,
         lastname = response.lastname,
         address = response.address,
         job = JobEntity(
            id = response.job.id,
            name = response.job.name
         ),
         phoneNumber = response.phoneNumber
      )
   }

   override suspend fun getEmployees(
      query: String?,
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
            job = JobEntity(
               id = employee.job.id,
               name = employee.job.name
            ),
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

   override suspend fun searchBy(
      query: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<EmployeeEntity> = wrapNetworkException {
      employeesApi.getEmployeesSearchBy(
         query = query,
         pageIndex = pageIndex,
         pageSize = pageSize
      ).data.map { employee ->
         EmployeeEntity(
            address = employee.address,
            firstname = employee.firstname,
            id = employee.id,
            job = JobEntity(
               id = employee.job.id,
               name = employee.job.name
            ),
            lastname = employee.lastname,
            phoneNumber = employee.phoneNumber
         )
      }
   }
}