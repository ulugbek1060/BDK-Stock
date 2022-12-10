package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.model.utils.liveData
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterOrderViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : ViewModel() {

   private val _currentFilterData = FilterOrdersFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .filterData

   private val _filterData = MutableLiveData(_currentFilterData)
   val filterData = _filterData.liveData()

   fun setFilterDate(
      status: Int,
      client: String,
      fromDate: String,
      toDate: String,
      driver: String
   ) {
      _filterData.value = _filterData.requireValue().copy(
         status = status,
         client = client,
         fromDate = fromDate,
         toDate = toDate,
         driver = driver
      )
   }

   fun getFilterData() = _filterData.requireValue()

   fun clearData() {
      _filterData.value = FilterData()
   }
}