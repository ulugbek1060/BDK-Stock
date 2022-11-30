package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import java.io.Serializable
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class IngredientsOperationsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = MutableLiveData(FilterOperations())
   val query = _query.liveData()

   val ingredientsExAndInFlow = _query
      .asFlow()
      .flatMapLatest { filter ->
         ingredientsRepository.getExpensesAndIncomesOfIngredients(
            operationsStatus = filter.status,
            fromDate = filter.fromDate,
            toDate = filter.toDate
         )
      }
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setFilterData(query: String?, status: Int?, fromDate: String?, toDate: String?) {
      _query.value = _query.requireValue().copy(
         query = query,
         status = status,
         fromDate = fromDate,
         toDate = toDate
      )
   }

   fun clearFilter(){
      _query.value = FilterOperations()
   }

   data class FilterOperations(
      val query: String? = null,
      val status: Int? = null,
      val fromDate: String? = null,
      val toDate: String? = null
   ) : Serializable
}