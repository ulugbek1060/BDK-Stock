package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterOrderViewModel @Inject constructor() : ViewModel() {

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

   private val _filterData = MutableLiveData(FilterData())

   fun getFilterData() = _filterData.requireValue()

   data class FilterData(
      val status: Int? = null,
      val client: String? = null,
      val fromDate: String? = null,
      val toDate: String? = null,
      val driver: String? = null,
   )


}