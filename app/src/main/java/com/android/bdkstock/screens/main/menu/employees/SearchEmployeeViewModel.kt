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
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchEmployeeViewModel @Inject constructor(
   private val employeeRepository: EmployeeRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _invalidate = MutableUnitLiveEvent()
   val invalidate = _invalidate.liveData()

   private val _showAuthError = MutableUnitLiveEvent()
   val showAuthError = _showAuthError.liveData()

   private val _query = savedStateHandle.getLiveData(EMPLOYEE_QUERY_KEY, "")
   val employeesFlow: Flow<PagingData<EmployeeEntity>> = _query.asFlow()
      .debounce(500)
      .flatMapLatest {
         employeeRepository.getEmployeeFromRemote(it)
      }
      .cachedIn(viewModelScope)

   fun setQuery(searchBy: String) {

      if (searchBy.isBlank())
         _invalidate.publishEvent()

      if (_query.requireValue() != searchBy)
         _query.value = searchBy
   }

   fun showAuthError() {
      _showAuthError.publishEvent()
   }

   private companion object {
      const val EMPLOYEE_QUERY_KEY = "employee_key"
   }
}