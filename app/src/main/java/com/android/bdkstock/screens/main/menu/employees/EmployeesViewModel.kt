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
import com.android.model.utils.Const.DEFAULT_DELAY
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class EmployeesViewModel @Inject constructor(
   private val employeesRepository: EmployeeRepository,
   savedStateHandle: SavedStateHandle,
   repository: AccountRepository
) : BaseViewModel(repository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.asLiveData()

   private val _query = savedStateHandle.getLiveData(EMPLOYEES_QUERY_KEY, "")

   val employeesFlow: Flow<PagingData<EmployeeEntity>> = _query.asFlow()
      .flatMapLatest {
         employeesRepository.getEmployeeFromRemote(it)
      }
      .cachedIn(viewModelScope)

   private var queryJob: Job? = null

   fun setQuery(query: String?) {
      if (_query.value == query) return

      queryJob?.cancel()
      queryJob = viewModelScope.launch {
         delay(DEFAULT_DELAY)
         _query.value = query
      }
   }

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   private companion object {
      const val EMPLOYEES_QUERY_KEY = "employees_query"
   }
}