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
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(EMPLOYEES_QUERY_KEY, "")

   val employeesFlow: Flow<PagingData<EmployeeEntity>> = _query.asFlow()
      .flatMapLatest {
         employeesRepository.getEmployeeFromRemote(it)
      }
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setQuery(query: String?) {
      if (_query.value == query) return

      _query.value = query
   }

   private companion object {
      const val EMPLOYEES_QUERY_KEY = "employees_query"
   }
}