package com.android.bdkstock.screens.main.menu.ingredients

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class IngredientsOperationsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _navigateToFilterFrag =
      MutableLiveEvent<FilterOperations>()
   val navigateToFilterFrag = _navigateToFilterFrag.liveData()

   private val _filterData = MutableLiveData(FilterOperations())

   val ingredientsExAndInFlow = _filterData
      .asFlow()
      .flatMapLatest { filter ->
         ingredientsRepository.getExpensesAndIncomesOfIngredients(
            operationsStatus = filter.status,
            fromDate = filter.fromDate,
            toDate = filter.toDate
         )
      }
      .cachedIn(viewModelScope)

   fun setFilterData(filterData: FilterOperations) {
      _filterData.value = filterData
   }

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun toFilterFrag() {
      _navigateToFilterFrag.publishEvent(_filterData.requireValue())
   }
}