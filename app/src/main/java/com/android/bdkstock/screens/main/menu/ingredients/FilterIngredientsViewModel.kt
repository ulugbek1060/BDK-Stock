package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.bdkstock.R
import com.android.model.utils.MutableLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterIngredientsViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : ViewModel() {

   private val _filterData = FilterIngredientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .ingredientsFilterData

   private val _filter = MutableLiveData(_filterData)
   val filter = _filter.asLiveData()

   private val _moveBack = MutableLiveEvent<IngredientsFilterData>()
   val moveBack = _moveBack.asLiveData()

   fun acceptFilterData(
      query: String,
      fromDate: String,
      toDate: String
   ) {
      val filterData = _filter.requireValue().copy(
         query = query,
         fromDate = fromDate,
         toDate = toDate
      )
      _filter.value = filterData
      _moveBack.publishEvent(filterData)
   }

   fun cancelFilterData() {
      val filterData = _filter.requireValue().copy(
         query = null,
         status = null,
         fromDate = null,
         toDate = null,
      )
      _filter.value = filterData
      _moveBack.publishEvent(filterData)
   }

   fun setStatus(status: Int?) {
      _filter.value = _filter.requireValue().copy(
         status = status
      )
   }
}

data class IngredientsFilterData(
   val query: String? = null,
   val status: Int? = null,
   val fromDate: String? = null,
   val toDate: String? = null
) : java.io.Serializable {

   fun getSelectedStatusId(): Int = when (status) {
      1 -> R.id.button_expense
      0 -> R.id.button_income
      else -> R.id.button_all
   }
}