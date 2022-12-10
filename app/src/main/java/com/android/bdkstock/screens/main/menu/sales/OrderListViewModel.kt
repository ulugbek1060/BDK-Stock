
package com.android.bdkstock.screens.main.menu.sales

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class OrderListViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _filterData = MutableLiveData(FilterData())
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

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setFilterData(filterData: FilterData) {
      _filterData.value = filterData
   }

   fun getFilterData(): FilterData {
      return _filterData.requireValue()
   }
}