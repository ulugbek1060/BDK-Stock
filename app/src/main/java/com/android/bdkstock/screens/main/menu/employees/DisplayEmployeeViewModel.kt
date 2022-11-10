package com.android.bdkstock.screens.main.menu.employees

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.employees.EmployeeRepository
import com.android.model.utils.MutableLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayEmployeeViewModel @Inject constructor(
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle,
   private val employeesRepository: EmployeeRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _showChangesError = MutableLiveEvent<String>()
   val showChangesError = _showChangesError.liveData()

   private val _employee = DisplayEmployeeFragmentArgs.fromSavedStateHandle(savedStateHandle)
   val employee = _employee.employeeEntity

   fun enableEditing() {
      _state.value = _state.requireValue().copy(
         isEditingEnable = true
      )
   }

   fun saveChanges(
      newFirstname: String,
      newLastname: String,
      newAddress: String,
      newPhoneNumber: String,
      newPassword: String
   ) = viewModelScope.safeLaunch {

   }

   private fun showProgress() {

   }

   private fun hideProgress() {

   }

   data class State(
      val isEditingEnable: Boolean = false,
      val progressIsActive: Boolean = false
   )
}