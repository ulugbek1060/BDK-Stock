package com.android.model.repository.sales

import androidx.paging.PagingData
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.repository.sales.entity.SimpleProduct
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SalesRepository @Inject constructor(
   private val salesSource: SalesSource
) : BaseRepository() {

   suspend fun createOrder(
      clientId: Long?,
      driverId: Long?,
      products: List<SimpleProduct>
   ): OrderEntity {

      if (clientId == null) throw EmptyFieldException(Field.EMPTY_CLIENT)
      if (driverId == null) throw EmptyFieldException(Field.EMPTY_DRIVER)
      if (products.isEmpty()) throw EmptyFieldException(Field.PRODUCT)

      return suspendRunCatching {
         salesSource.createOrder(clientId, driverId, products)
      }
   }

   suspend fun payForOrder(
      orderId: Long?,
      cash: String,
      card: String
   ): String {

      if (orderId == null) throw EmptyFieldException(Field.EMPTY_ORDER)
      if (cash.isBlank()) throw EmptyFieldException(Field.CASH)
      if (card.isBlank()) throw EmptyFieldException(Field.CARD)

      return suspendRunCatching {
         salesSource.payForOrder(orderId, cash, card)
      }
   }

   fun getOrdersList(
      status: String? = null,
      client: String? = null,
      fromDate: String? = null,
      toDate: String? = null,
      driver: String? = null,
   ): Flow<PagingData<OrderListItem>> = getPagerData {
      val loader: DataLoader<OrderListItem> = { pageIndex ->
         salesSource.getOrdersList(
            status = status,
            client = client,
            fromDate = fromDate,
            toDate = toDate,
            driver = driver,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE
         )
      }
      BasePageSource(loader, DEFAULT_PAGE_SIZE)
   }

   suspend fun getOrderOverview(orderId: Long?): OrderEntity {
      if (orderId == null) throw EmptyFieldException(Field.EMPTY_ORDER)

      return suspendRunCatching {
         salesSource.getOrderOverview(orderId)
      }
   }
}