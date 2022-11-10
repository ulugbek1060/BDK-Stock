package com.android.bdkstock.screens.main.menu.employees

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.employees.EmployeeRepository
import com.android.model.repository.employees.entity.EmployeeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class EmployeesViewModel @Inject constructor(
   private val repository: AccountRepository,
   private val employeesRepository: EmployeeRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(repository) {

   val employeesFlow: Flow<PagingData<EmployeeEntity>>

   private val query = savedStateHandle.getLiveData("query", "")

   fun setQuery(query: String) {
      this.query.value = query
   }

   init {
      employeesFlow = query.asFlow()
         .debounce(500)
         .flatMapLatest {
            employeesRepository.getEmployeesFromLocal(it)
         }
         // always use cacheIn operator for flows returned by Pager. Otherwise exception may be thrown
         // when 1) refreshing/invalidating or 2) subscribing to the flow more than once.
         .cachedIn(viewModelScope)
   }

}