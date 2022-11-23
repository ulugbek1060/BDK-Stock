package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.MutableLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterOperationsViewModel @Inject constructor(
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _filterData = MutableLiveData(FilterOperations())
   val filterData = _filterData.liveData()

   private val _goBack = MutableLiveEvent<FilterOperations>()
   val goBack = _goBack.liveData()

   private val _currentFilter = FilterOperationsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .filterData

   init {
      _filterData.value = _currentFilter
   }

   fun setStatus(status: Int?) {
      _filterData.value = _filterData.requireValue().copy(
         status = status
      )
   }

   fun setData(fromDate: String, toDate: String) {
      _filterData.value = _filterData.requireValue().copy(
         fromDate = fromDate,
         toDate = toDate
      )
      _goBack.publishEvent(_filterData.requireValue())
   }

   fun clearFilter() {
      _filterData.value = FilterOperations()
      _goBack.publishEvent(_filterData.requireValue())
   }
}