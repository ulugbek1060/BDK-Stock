package com.android.source.network.employees

import com.android.source.network.employees.entity.employeeslist.EmployeesResponseEntity
import com.android.source.network.employees.entity.registeremplyees.RegisterEmployeeRequestEntity
import com.android.source.network.employees.entity.registeremplyees.RegisterEmployeeResponseEntity
import com.android.source.network.employees.entity.updateemployees.UpdateEmployeeRequestEntity
import com.android.source.network.employees.entity.updateemployees.UpdateEmployeeResponseEntity
import retrofit2.http.*

interface EmployeesApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/register")
   suspend fun registerEmployee(@Body body: RegisterEmployeeRequestEntity): RegisterEmployeeResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/get")
   suspend fun getEmployees(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): EmployeesResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/user/update/{id}")
   suspend fun updateEmployee(
      @Path("id") id: Long,
      @Body body: UpdateEmployeeRequestEntity
   ): UpdateEmployeeResponseEntity
}