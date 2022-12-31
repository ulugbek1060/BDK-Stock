package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
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
   val errorEvent = _errorEvent.asLiveData()

   private val _filter = MutableLiveData(IngredientsFilterData())
   val filter = _filter.asLiveData()

   val ingredientsExAndInFlow = _filter
      .asFlow()
      .flatMapLatest { filter ->
         ingredientsRepository.getExpensesAndIncomesOfIngredients(
            query = filter.query,
            operationsStatus = filter.status,
            fromDate = filter.fromDate,
            toDate = filter.toDate
         )
      }
      .cachedIn(viewModelScope)

   fun getFilter() = _filter.requireValue()

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setFilterData(filterData: IngredientsFilterData) {
      _filter.value = filterData
   }
}