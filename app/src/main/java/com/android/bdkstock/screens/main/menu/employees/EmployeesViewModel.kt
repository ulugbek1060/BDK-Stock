package com.android.bdkstock.screens.main.menu.employees

import androidx.lifecycle.SavedStateHandle
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
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class EmployeesViewModel @Inject constructor(
   private val repository: AccountRepository,
   private val employeesRepository: EmployeeRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(repository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   val employeesFlow: Flow<PagingData<EmployeeEntity>> = employeesRepository
      .getEmployeesFromLocal()
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

}