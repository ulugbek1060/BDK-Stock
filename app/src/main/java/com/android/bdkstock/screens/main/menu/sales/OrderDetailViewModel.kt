package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

   private val _payErrorEvent = MutableUnitLiveEvent()
   val payErrorEvent = _payErrorEvent.liveData()

   private val orderResult = salesRepository
      .getOrderOverview(_orderId)

   init {
      viewModelScope.safeLaunch {
         showProgress()
         try {
            orderResult.collectLatest { setOrderEntity(it) }
         } catch (e: EmptyFieldException) {
            showEmptyError(e)
         } finally {
            hideProgress()
         }
      }
   }

   fun pay(cash: String, card: String) = viewModelScope.safeLaunch {
      showProgress()
      try {
         delay(2000)
         salesRepository.payForOrder(
            orderId = _orderId,
            cash = cash,
            card = card
         )
      } catch (e: EmptyFieldException) {
         _payErrorEvent.publishEvent()
      } finally {
         hideProgress()
      }
   }

   private fun showEmptyError(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         isEmptyOrderId = e.field == Field.EMPTY_ORDER
      )
   }

   private fun setOrderEntity(orderEntity: OrderEntity?) {
      _state.value = _state.requireValue().copy(
         orderEntity = orderEntity
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

   fun getOrderEntity(): OrderEntity? {
      return _state.requireValue().orderEntity
   }

   data class State(
      val isInProgress: Boolean = false,
      val isEmptyOrderId: Boolean = false,
      val orderEntity: OrderEntity? = null
   )
}