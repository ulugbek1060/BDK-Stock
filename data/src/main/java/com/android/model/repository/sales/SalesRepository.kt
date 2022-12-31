package com.android.model.repository.sales

import androidx.paging.PagingData
import com.android.model.di.IoDispatcher
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.repository.sales.entity.SimpleProduct
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.android.model.utils.wrapBackendExceptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SalesRepository @Inject constructor(
   private val salesSource: SalesSource,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository() {

   suspend fun createOrder(
      clientId: Long?,
      driverId: Long?,
      products: List<SimpleProduct>
   ): OrderEntity {

      if (clientId == null) throw EmptyFieldException(Field.EMPTY_CLIENT)
      if (driverId == null) throw EmptyFieldException(Field.EMPTY_DRIVER)
      if (products.isEmpty()) throw EmptyFieldException(Field.PRODUCT)

      return wrapBackendExceptions {
         salesSource.createOrder(clientId, driverId, products)
      }
   }

   suspend fun payForOrder(
      orderId: Long?,
      cash: String,
      card: String
   ): String {

      if (orderId == null) throw EmptyFieldException(Field.EMPTY_ORDER)

      return if (cash.isBlank() && card.isNotBlank()) {
         wrapBackendExceptions {
            salesSource.payForOrder(orderId, cash, card)
         }
      } else if (card.isBlank() && cash.isNotBlank()) {
         wrapBackendExceptions {
            salesSource.payForOrder(orderId, cash, card)
         }
      } else if (cash.isNotBlank() && card.isNotBlank()) {
         wrapBackendExceptions {
            salesSource.payForOrder(orderId, cash, card)
         }
      } else {
         throw EmptyFieldException(Field.EMPTY_PAY_FIELD)
      }
   }

   suspend fun cancelOrder(orderId: Long) {
      salesSource.cancelOrder(orderId)
   }

   fun getOrdersList(
      status: Int? = null,
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

   fun getOrderOverview(orderId: Long?): Flow<OrderEntity> = flow {
      if (orderId == null) throw EmptyFieldException(Field.EMPTY_ORDER)
      emit(wrapBackendExceptions { salesSource.getOrderOverview(orderId) })
   }.flowOn(ioDispatcher)

}