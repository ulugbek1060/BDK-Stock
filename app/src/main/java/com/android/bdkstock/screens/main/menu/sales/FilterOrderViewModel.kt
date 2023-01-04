package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.bdkstock.R
import com.android.model.utils.asLiveData
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class FilterOrderViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : ViewModel() {

   private val _currentFilterData = FilterOrdersFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .ordersFilterData

   private val _filterData = MutableLiveData(_currentFilterData)
   val filterData = _filterData.asLiveData()

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

   fun getFilterData() = _filterData.requireValue()!!

   fun clearData() {
      _filterData.value = OrdersFilterData()
   }
}

data class OrdersFilterData(
   val status: Int? = null,
   val client: String? = null,
   val fromDate: String? = null,
   val toDate: String? = null,
   val driver: String? = null,
) : Serializable {

   fun getSelectedStatusId(): Int = when (status) {
      ORDER_WAITING_FOR_PAYMENT -> R.id.button_waiting
      ORDER_SOLD -> R.id.button_sold
      ORDER_CANCELED -> R.id.button_cancel
      else -> R.id.button_all
   }


   companion object {
      const val ORDER_WAITING_FOR_PAYMENT = 0
      const val ORDER_SOLD = 1
      const val ORDER_CANCELED = 3
   }
}