package com.android.source.network.sales

import com.android.source.network.sales.entity.createorder.OrderCreateRequestEntity
import com.android.source.network.sales.entity.createorder.OrderCreateResponseEntity
import com.android.source.network.sales.entity.detailorder.OrderDetailResponseEntity
import com.android.source.network.sales.entity.orderslist.OrderListResponseEntity
import com.android.source.network.sales.entity.payfororder.OrderPayRequestEntity
import com.android.source.network.sales.entity.payfororder.OrderPayResponseEntity
import retrofit2.http.*

interface SalesApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/order/create")
   suspend fun createOrder(
      @Body body: OrderCreateRequestEntity
   ): OrderCreateResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/order/order-pay")
   suspend fun payForOrder(
      @Body body: OrderPayRequestEntity
   ): OrderPayResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/order/order-cancel/{id}")
   suspend fun cancelOrder(
      @Path("id") orderId: Long
   )

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/order/order-list")
   suspend fun getOrders(
      @Query("status") status: Int?,
      @Query("client") client: String?,
      @Query("dateFrom") fromDate: String?,
      @Query("dateTo") toDate: String?,
      @Query("auto_number") driver: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): OrderListResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/order/order/{id}")
   suspend fun getOrderDetail(
      @Path("id") orderId: Long
   ): OrderDetailResponseEntity
}