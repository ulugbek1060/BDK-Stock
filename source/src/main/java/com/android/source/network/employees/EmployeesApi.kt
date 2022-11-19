package com.android.source.network.employees

import com.android.model.utils.Const.HEADER_KEY_ACCEPT
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.employees.entity.get.EmployeesResponseEntity
import com.android.source.network.employees.entity.register.RegisterEmployeeRequestEntity
import com.android.source.network.employees.entity.register.RegisterEmployeeResponseEntity
import com.android.source.network.employees.entity.searchby.EmployeeSearchByResponseEntity
import com.android.source.network.employees.entity.update.UpdateEmployeeRequestEntity
import com.android.source.network.employees.entity.update.UpdateEmployeeResponseEntity
import retrofit2.http.*

interface EmployeesApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/get")
   suspend fun getEmployees(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): EmployeesResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/get")
   suspend fun getEmployeesSearchBy(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): EmployeeSearchByResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/user/update/{id}")
   suspend fun updateEmployee(
      @Path("id") id: Long,
      @Body body: UpdateEmployeeRequestEntity
   ): UpdateEmployeeResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/register")
   suspend fun registerEmployee(@Body body: RegisterEmployeeRequestEntity): RegisterEmployeeResponseEntity

}