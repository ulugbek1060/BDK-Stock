package com.android.model.repository.sales

import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.repository.sales.entity.SimpleProduct

interface SalesSource {

   /**
    * Creates a order
    * Arguments: [Long], [Long], [List]
    * @return [OrderEntity]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun createOrder(
      clientId: Long,
      driverId: Long,
      products: List<SimpleProduct>
   ): OrderEntity

   /**
    * Pays for order
    * Arguments: orderId [Long], cash [String], card [String]
    * @return [String] success message or sth
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun payForOrder(
      orderId: Long,
      cash: String,
      card: String
   ): String

   // TODO: need to define call back object or property
   /**
    * Cancels order and return back all products and payed summs
    * Arguments: orderId [Long]
    * @return
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun cancelOrder(
      orderId: Long
   )

   /**
    * Fetches all order item with pagination
    * Arguments: status [String], client [String], fromDate [String],
    * toDate [String], driver [String], pagIndex [Int], pageSize [Int]
    * @return [List] of [OrderListItem]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getOrdersList(
      status: Int?,
      client: String?,
      fromDate: String?,
      toDate: String?,
      driver: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<OrderListItem>

   /**
    * Fetches order by its identification
    * Arguments: orderId [Long]
    * @return [OrderEntity]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getOrderOverview(orderId: Long): OrderEntity

}