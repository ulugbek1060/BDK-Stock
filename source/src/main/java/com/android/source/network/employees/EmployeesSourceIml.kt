package com.android.source.network.employees

import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class EmployeesSourceIml @Inject constructor(
   private val employeesApi: EmployeesApi
) : BaseNetworkSource(), EmployeesSource {

   private val TAG = this.javaClass.simpleName

   override suspend fun getEmployees(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<EmployeeEntity> {

      return employeesApi.getEmployees(
         query = query,
         pageIndex = pageIndex,
         pageSize = pageSize
      ).data.map { employee ->
         EmployeeEntity(
            address = employee.address,
            firstname = employee.firstname,
            id = employee.id,
            jobTitle = employee.jobTitle,
            lastname = employee.lastname,
            phoneNumber = employee.phoneNumber
         )
      }
   }

   override fun getEmployeeById() {}
}