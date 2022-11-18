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

   @GET("api/user/get")
   suspend fun getEmployees(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): EmployeesResponseEntity

   @GET("api/user/get")
   suspend fun getEmployeesSearchBy(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): EmployeeSearchByResponseEntity

   @POST("api/user/update/{id}")
   suspend fun updateEmployee(
      @Path("id") id: Long,
      @Body body: UpdateEmployeeRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): UpdateEmployeeResponseEntity

   @POST("api/register")
   suspend fun registerEmployee(
      @Body body: RegisterEmployeeRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): RegisterEmployeeResponseEntity

}