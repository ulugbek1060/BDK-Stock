package com.android.source.network.employees

import com.android.model.utils.Const.HEADER_KEY_ACCEPT
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.employees.entity.EmployeesResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EmployeesApi {

   @GET("api/user/get")
   suspend fun getEmployees(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): EmployeesResponseEntity

}