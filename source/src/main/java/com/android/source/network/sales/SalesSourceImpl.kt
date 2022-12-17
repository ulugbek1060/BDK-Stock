package com.android.source.network.sales

import com.android.model.repository.sales.SalesSource
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.repository.sales.entity.OrderedProduct
import com.android.model.repository.sales.entity.SimpleProduct
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.sales.entity.createorder.OrderCreateRequestEntity
import com.android.source.network.sales.entity.createorder.ProductForCreateOrder
import com.android.source.network.sales.entity.payfororder.OrderPayRequestEntity
import javax.inject.Inject

class SalesSourceImpl @Inject constructor(
   private val salesApi: SalesApi
) : BaseNetworkSource(), SalesSource {

   override suspend fun createOrder(
      clientId: Long, driverId: Long, products: List<SimpleProduct>
   ): OrderEntity = wrapNetworkException {
      val body = OrderCreateRequestEntity(
         clientId = clientId,
         driverId = driverId,
         products = products.map {
            ProductForCreateOrder(
               id = it.id, amount = it.amount
            )
         })
      val order = salesApi.createOrder(body).data
      OrderEntity(
         id = order.id,
         identification = order.orderIdentifier,
         status = order.status,
         summa = order.summa,
         paid = order.paid,
         debit = order.debit,
         createdAt = order.createdAt,
         client = order.client.toClientForEntity(),
         driver = order.driver.toDriverForOrderEntity(),
         products = order.products.map {
            OrderedProduct(
               id = it.id,
               name = it.name,
               unit = it.unit,
               amount = it.amount,
               price = it.price,
               summa = it.summa
            )
         }
      )
   }

   override suspend fun payForOrder(orderId: Long, cash: String, card: String): String =
      wrapNetworkException {
         val body = OrderPayRequestEntity(
            orderId = orderId,
            card = card,
            cash = cash
         )
         salesApi
            .payForOrder(body)
            .message
      }

   override suspend fun cancelOrder(orderId: Long) = wrapNetworkException {
      salesApi.cancelOrder(orderId)
   }

   override suspend fun getOrdersList(
      status: Int?,
      client: String?,
      fromDate: String?,
      toDate: String?,
      driver: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<OrderListItem> = wrapNetworkException {
      salesApi.getOrders(
         status = status,
         client = client,
         fromDate = fromDate,
         toDate = toDate,
         driver = driver,
         pageIndex = pageIndex,
         pageSize = pageSize
      )
         .orders
         .orderList
         .map {
            OrderListItem(
               id = it.id,
               identification = it.name,
               client = it.client,
               summa = it.summa,
               status = it.status,
               createdAt = it.createdAt
            )
         }
   }

   override suspend fun getOrderOverview(orderId: Long): OrderEntity = wrapNetworkException {
      val order = salesApi
         .getOrderDetail(orderId = orderId)
         .orderDetail

      OrderEntity(
         id = order.id,
         identification = order.identification,
         status = order.status,
         summa = order.summa,
         paid = order.paid,
         debit = order.debit,
         createdAt = order.createdAt,
         client = order.client.toClientForEntity(),
         driver = order.driver.toDriverForOrderEntity(),
         products = order.products.map {
            OrderedProduct(
               id = it.id,
               name = it.name,
               unit = it.unit,
               amount = it.amount,
               price = it.price,
               summa = it.summa
            )
         }
      )
   }
}