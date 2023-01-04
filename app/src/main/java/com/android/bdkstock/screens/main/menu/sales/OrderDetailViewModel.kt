package com.android.bdkstock.screens.main.menu.sales

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.di.IoDispatcher
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.utils.*
import com.android.model.utils.Const.PERM_CANCEL_ORDER
import com.android.model.utils.Const.PERM_PAY_FOR_ORDER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   private val accountRepository: AccountRepository,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _orderId = OrderDetailFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .orderId

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _navigatePaymentsFrag = MutableLiveEvent<OrderEntity?>()
   val navigatePaymentsFrag = _navigatePaymentsFrag.asLiveData()

   private val orderResult = salesRepository
      .getOrderOverview(_orderId)

   init {
      getUserPerms()
      getOrder()
   }

   //user permissions
   private fun getUserPerms() {
      viewModelScope.launch(ioDispatcher) {
         accountRepository.getUserPermissions().collectLatest { result ->
            val payForOrderPerm =
               result.getValueOrNull()?.lastOrNull { it == PERM_PAY_FOR_ORDER } != null
            val cancelOrderPerm =
               result.getValueOrNull()?.lastOrNull { it == PERM_CANCEL_ORDER } != null
            _state.postValue(
               _state.requireValue().copy(
                  payOrderPerm = payForOrderPerm,
                  cancelOrderPerm = cancelOrderPerm
               )
            )
         }
      }
   }

   fun getPayForOrderPerm() = _state.requireValue().payOrderPerm
   fun getCancelOrderPerm() = _state.requireValue().cancelOrderPerm

   fun getOrder() = viewModelScope.safeLaunch {
      showProgress()
      try {
         orderResult.collectLatest {
            _state.value = _state.requireValue().copy(orderEntity = it)
         }
      } catch (e: EmptyFieldException) {
         showEmptyError(e)
      } finally {
         hideProgress()
      }
   }

   fun payment() {
      _navigatePaymentsFrag.publishEvent(getOrderEntity())
   }

   // TODO: need to fix
   fun cancelOrder() = viewModelScope.safeLaunch {
      salesRepository.cancelOrder(_orderId)
   }

   private fun getOrderEntity(): OrderEntity? {
      return _state.requireValue().orderEntity
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

   fun statusIsEqualZero(): Boolean {
      return _state.requireValue()?.orderEntity?.status == 0
   }

   data class State(
      val orderEntity: OrderEntity? = null,
      val isInProgress: Boolean = false,
      val isEmptyOrderId: Boolean = false,
      val cancelOrderPerm: Boolean = false,
      val payOrderPerm: Boolean = false
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

      fun fabVisibility(): Boolean? {
         return if (orderEntity?.status != null) {
            orderEntity.status != 1
         } else {
            null
         }
      }

      fun getIdentification() =
         if (orderEntity == null) "№:..."
         else "№: ${orderEntity.identification}"

   }
}