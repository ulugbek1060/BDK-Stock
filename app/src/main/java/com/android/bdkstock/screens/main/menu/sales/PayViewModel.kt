package com.android.bdkstock.screens.main.menu.sales

import android.content.Context
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
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val orderEntity = PayFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .orderEntity

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _order = MutableLiveData<OrderEntity?>()
   val order = _order.asLiveData()

   private val _goBack = MutableLiveEvent<String>()
   val goBack = _goBack.asLiveData()

   init {
      _order.value = orderEntity
   }

   fun pay(cash: String, card: String) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val message = salesRepository.payForOrder(
            orderId = orderEntity.id,
            cash = cash,
            card = card
         )
         successAndClosePage(message)
      } catch (e: EmptyFieldException) {
         showEmptyError(e)
      } finally {
         hideProgress()
      }
   }

   private fun successAndClosePage(message: String) {
      _goBack.publishEvent(message)
   }

   private fun showEmptyError(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         isEmptyPay = e.field == Field.EMPTY_PAY_FIELD
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
      val isEmptyPay: Boolean = false,

      ) {
      fun getEmptyPayError(context: Context) =
         if (isEmptyPay) context.getString(R.string.error_empty_pay_field)
         else null
   }
}