package com.android.model.repository.employees

import com.android.model.repository.employees.entity.EmployeeEntity


interface EmployeesSource {

   /**
    * registers employee
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun registerEmployee(
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      jobId: Int
   ): String // message

   /**
    * gets all registered employees
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getEmployees(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<EmployeeEntity> // list

   /**
    * updates existing employee
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun updateEmployee(
      id:Long,
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      password: String,
      job: Int
   ): String // message

}