package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.di.IoDispatcher
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.utils.Const.PERM_NEW_ORDER
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrderListViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   private val accountRepository: AccountRepository,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _filterData = MutableLiveData(OrdersFilterData())
   val ordersFlow = _filterData.asFlow()
      .flatMapLatest { filter ->
         salesRepository.getOrdersList(
            status = filter.status,
            client = filter.client,
            fromDate = filter.fromDate,
            toDate = filter.toDate,
            driver = filter.driver
         )
      }
      .cachedIn(viewModelScope)

   init {
      getUserPermissions()
   }

   private fun getUserPermissions() {
      viewModelScope.launch(ioDispatcher) {
         accountRepository
            .getUserPermissions()
            .collectLatest { result ->
               val newOrderPerm =
                  result.getValueOrNull()?.lastOrNull { it == PERM_NEW_ORDER } != null
               _state.postValue(
                  _state.requireValue().copy(
                     createNewOrder = newOrderPerm
                  )
               )
            }
      }
   }

   fun getNewOrderPerm(): Boolean = _state.requireValue().createNewOrder

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setFilterData(filterData: OrdersFilterData) {
      _filterData.value = filterData
   }

   fun getFilterData(): OrdersFilterData {
      return _filterData.requireValue()
   }

   data class State(
      val createNewOrder: Boolean = false
   )
}