package com.android.model.repository.employees

import com.android.model.repository.employees.entity.EmployeeEntity


interface EmployeesSource {

   /**
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getEmployees(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<EmployeeEntity>

   fun getEmployeeById()
}