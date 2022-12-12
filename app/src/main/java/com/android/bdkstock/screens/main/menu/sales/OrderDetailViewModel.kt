package com.android.bdkstock.screens.main.menu.sales

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _orderId = OrderDetailFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .orderId

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _order = MutableLiveData(OrderData())
   val order = _order.liveData()

   private val _navigatePaymentsFrag = MutableLiveEvent<OrderEntity?>()
   val navigatePaymentsFrag = _navigatePaymentsFrag.liveData()

   private val orderResult = salesRepository
      .getOrderOverview(_orderId)

   init {
      getOrder()
   }

   fun getOrder() = viewModelScope.safeLaunch {
      showProgress()
      try {
         orderResult.collectLatest { _order.value = OrderData(it) }
      } catch (e: EmptyFieldException) {
         showEmptyError(e)
      } finally {
         hideProgress()
      }
   }

   fun payment() {
      _navigatePaymentsFrag.publishEvent(getOrderEntity())
   }

   private fun getOrderEntity(): OrderEntity? {
      return _order.requireValue().orderEntity
   }

   private fun showEmptyError(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         isEmptyOrderId = e.field == Field.EMPTY_ORDER
      )
   }

   private fun showProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = true
      )
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = false
      )
   }

   data class State(
      val isInProgress: Boolean = false,
      val isEmptyOrderId: Boolean = false
   )

   data class OrderData(
      val orderEntity: OrderEntity? = null
   ) {
      @SuppressLint("UseCompatLoadingForDrawables")
      fun getStatusIndicator(context: Context) =
         if (orderEntity?.status == 1) context.getDrawable(R.drawable.ic_sales)
         else context.getDrawable(R.drawable.ic_time)

      fun getStatusText(context: Context) =
         if (orderEntity?.status == 1) context.getString(R.string.sale_status_1)
         else context.getString(R.string.sale_status_0)

      fun getStatusColor(context: Context) =
         if (orderEntity?.status == 1) context.getColor(R.color.green)
         else context.getColor(R.color.orange)

      fun fabVisibility(view: View) =
         if (orderEntity?.status == 1) view.gone()
         else view.visible()

      fun getIdentification() =
         if (orderEntity == null) "№:..."
         else "№: ${orderEntity.identification}"

   }
}