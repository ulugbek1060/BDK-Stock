package com.android.source.network.dashboard

import com.android.source.network.dashboard.entity.ChartInfoResponseEntity
import com.android.source.network.dashboard.entity.GetPaysResponseEntity
import com.android.source.network.dashboard.entity.TableRequestEntity
import com.android.source.network.dashboard.entity.paychecklist.PaycheckResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DashboardApi {

   //cards api
   @GET("api/dashboard/pays")
   suspend fun getSumInfo(): GetPaysResponseEntity

   //chart api
   @POST("/api/dashboard/pays-table")
   suspend fun getTableInfo(@Body body: TableRequestEntity): ChartInfoResponseEntity

   @GET("/api/dashboard/order-pay")
   suspend fun paycheckList(
      @Query("page") pageIndex: Int
   ): PaycheckResponseEntity

   @GET("/api/dashboard/arr-pay")
   suspend fun expenditureList(
      @Query("page") pageIndex: Int
   ): PaycheckResponseEntity

}