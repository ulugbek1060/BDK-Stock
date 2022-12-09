package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.android.model.utils.liveData
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
   salesRepository: SalesRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _orderId = OrderDetailFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .orderId

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val orderResult = salesRepository
      .getOrderOverview(_orderId)

   init {
      viewModelScope.safeLaunch {
         showProgress()
         try {
            delay(1000)
            orderResult.collectLatest { setOrderEntity(it) }
         } catch (e: EmptyFieldException) {
            showEmptyError(e)
         } finally {
            hideProgress()
         }
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

   data class State(
      val isInProgress: Boolean = false,
      val isEmptyOrderId: Boolean = false,
      val orderEntity: OrderEntity? = null
   )
}