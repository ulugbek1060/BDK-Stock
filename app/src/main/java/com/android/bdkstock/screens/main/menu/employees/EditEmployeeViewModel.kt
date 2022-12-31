package com.android.bdkstock.screens.main.menu.employees

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.employees.EmployeeRepository
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.repository.jobs.JobRepository
import com.android.model.repository.jobs.entity.JobEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class EditEmployeeViewModel @Inject constructor(
   private val employeesRepository: EmployeeRepository,
   private val jobsRepository: JobRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentEmployee = EditEmployeeFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .employeeEntity

   private val _employee = MutableLiveData<EmployeeEntity>()
   val employee = _employee.asLiveData()

   private val _jobs = MutableLiveData<List<JobEntity>>()
   val jobs = _jobs.asLiveData()

   private val _navigateBack = MutableLiveEvent<EmployeeEntity>()
   val navigateBack = _navigateBack.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   init {
      _employee.value = _currentEmployee

      viewModelScope.safeLaunch {
         jobsRepository.getJobs().collectLatest {
            val list = it.getValueOrNull() ?: emptyList()
            _jobs.value = list
         }
      }
   }

   fun updateEmployee(
      newFirstname: String,
      newLastname: String,
      newAddress: String,
      newPhoneNumber: String,
      newPassword: String,
      newPasswordConfirm: String,
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         employeesRepository.updateEmployee(
            id = getEmployeeId(),
            firstname = newFirstname,
            lastname = newLastname,
            phoneNumber = newPhoneNumber,
            address = newAddress,
            password = newPassword,
            confirmPassword = newPasswordConfirm,
            jobId = getJobId()
         )
         _employee.value = _employee.requireValue().copy(
            firstname = newFirstname,
            lastname = newLastname,
            phoneNumber = formatPhoneNumber(newPhoneNumber),
            address = newAddress
         )
         _navigateBack.publishEvent(_employee.requireValue())
      } catch (e: EmptyFieldException) {
         publishEmptyFieldError(e)
      } finally {
         hideProgress()
      }
   }

   fun setJobEntity(job: JobEntity) {
      _employee.value = _employee.requireValue().copy(
         job = job
      )
   }

   private fun publishEmptyFieldError(e: EmptyFieldException) {
      _state.value = _state.value?.copy(
         emptyFirstnameError = e.field == Field.FIRSTNAME,
         emptyLastnameError = e.field == Field.LASTNAME,
         emptyAddressError = e.field == Field.ADDRESS,
         emptyPhoneNumberError = e.field == Field.PHONE_NUMBER,
         passwordMismatch = e.field == Field.MATCH_PASSWORD_FIELDS
      )
   }

   private fun formatPhoneNumber(number: String): String {
      return try {
         if (number.contains("+998")) {
            number.removePrefix("+998")
         } else if (number.contains("998")) {
            number.removePrefix("998")
         } else {
            number
         }
      } catch (e: Exception) {
         "phone number is invalid"
      }
   }

   private fun getEmployeeId() = _employee.requireValue().id

   private fun getJobId() = _employee.requireValue().job.id

   private fun showProgress() {
      _state.value = _state.value?.copy(
         isInProgress = true
      )
   }

   private fun hideProgress() {
      _state.value = _state.value?.copy(
         isInProgress = false
      )
   }

   data class State(
      val isInProgress: Boolean = false,
      val emptyFirstnameError: Boolean = false,
      val emptyLastnameError: Boolean = false,
      val emptyAddressError: Boolean = false,
      val emptyPhoneNumberError: Boolean = false,
      val passwordMismatch: Boolean = false,
   ) {
      fun getNameError(context: Context) =
         if (emptyFirstnameError) context.getString(R.string.error_empty_name)
         else null

      fun getSurnameError(context: Context) =
         if (emptyLastnameError) context.getString(R.string.error_empty_surname)
         else null

      fun getAddressError(context: Context) =
         if (emptyAddressError) context.getString(R.string.error_empty_address)
         else null

      fun getPhoneNumberError(context: Context) =
         if (emptyPhoneNumberError) context.getString(R.string.error_empty_phone_number)
         else null

      fun getPasswordError(context: Context) =
         if (passwordMismatch) context.getString(R.string.error_empty_password)
         else null
   }

}