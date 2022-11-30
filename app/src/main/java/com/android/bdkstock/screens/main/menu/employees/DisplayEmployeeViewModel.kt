package com.android.bdkstock.screens.main.menu.employees

import android.content.Context
import android.util.Log
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
class DisplayEmployeeViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle,
   accountRepository: AccountRepository,
   private val employeesRepository: EmployeeRepository,
   private val jobsRepository: JobRepository
) : BaseViewModel(accountRepository) {

   private val TAG = "DisplayEmployeeViewMode"

   private val _currentEmployee =
      DisplayEmployeeFragmentArgs.fromSavedStateHandle(savedStateHandle)

   private val _showSuggestionDialog = MutableUnitLiveEvent()
   val showSuggestionDialog = _showSuggestionDialog.liveData()

   private val _jobsEntity = MutableLiveData<List<JobEntity>>(emptyList())
   val jobsEntity = _jobsEntity.liveData()

   private val _employeeEntity = MutableLiveData<EmployeeEntity>()
   val employeeEntity = _employeeEntity.liveData()

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _showMessages = MutableLiveEvent<String>()
   val showMessages = _showMessages.liveData()

   private val _clearPassword = MutableUnitLiveEvent()
   val clearPassword = _clearPassword.liveData()

   init {
      viewModelScope.safeLaunch {
         jobsRepository.getJobs().collectLatest { result ->
            if (result is Success) _jobsEntity.value = result.value ?: emptyList()
         }
      }
      getInitialValue()
   }

   /**
    * 1. initial user data
    */
   private fun getInitialValue() = try {
      val phoneNumber = _currentEmployee.employeeEntity.phoneNumber
      val entity = _currentEmployee.employeeEntity.copy(
         phoneNumber = formatPhoneNumber(phoneNumber)
      )
      _employeeEntity.value = entity
   } catch (e: Exception) {
      e.printStackTrace()
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
            val message = employeesRepository.updateEmployee(
               id = getEmployeeId(),
               firstname = newFirstname,
               lastname = newLastname,
               phoneNumber = newPhoneNumber,
               address = newAddress,
               password = newPassword,
               confirmPassword = newPasswordConfirm,
               jobId = getJobId()
            )
            Log.d(TAG, "updateEmployee: $message")
            successResultAndRestoreState(message)
         } catch (e: EmptyFieldException) {
            publishEmptyFieldError(e)
         } finally {
            hideProgress()
         }
   }

   fun toggleChangeableState() {
      if (!changeableState()) {
         _showSuggestionDialog.publishEvent()
      } else {
         disableChangeableState()
      }
   }

   fun enableChangeableState() {
      _state.value = _state.requireValue().copy(
         isChangeableEnable = true
      )
   }

   fun disableChangeableState() {
      _state.value = _state.requireValue().copy(
         isChangeableEnable = false
      )
      getInitialValue()
   }

   fun setJobEntity(job: JobEntity) {
      _employeeEntity.value = _employeeEntity.requireValue().copy(
         job = job
      )
   }

   private fun changeableState() = _state.requireValue().isChangeableEnable

   private fun getEmployeeId() = _employeeEntity.requireValue().id

   private fun getJobId() = _employeeEntity.requireValue().job.id.toInt()

   private fun successResultAndRestoreState(message: String) {
      publishMessage(message)
      _state.postValue(State())
      _clearPassword.publishEvent()
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

   private fun publishEmptyFieldError(e: EmptyFieldException) {
      _state.value = _state.value?.copy(
         emptyFirstnameError = e.field == Field.FIRSTNAME,
         emptyLastnameError = e.field == Field.LASTNAME,
         emptyAddressError = e.field == Field.ADDRESS,
         emptyPhoneNumberError = e.field == Field.PHONE_NUMBER,
         emptyPasswordError = e.field == Field.PASSWORD,
         passwordMismatch = e.field == Field.MATCH_PASSWORD_FIELDS
      )
   }

   private fun showProgress() {
      _state.value = _state.value?.copy(
         isProgressActive = true
      )
   }

   private fun hideProgress() {
      _state.value = _state.value?.copy(
         isProgressActive = false
      )
   }

   private fun publishMessage(message: String) = _showMessages.publishEvent(message)


   data class State(
      val emptyFirstnameError: Boolean = false,
      val emptyLastnameError: Boolean = false,
      val emptyAddressError: Boolean = false,
      val emptyPhoneNumberError: Boolean = false,
      val emptyPasswordError: Boolean = false,
      val passwordMismatch: Boolean = false,
      val isChangeableEnable: Boolean = false,
      val isProgressActive: Boolean = false
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
         if (emptyPasswordError) context.getString(R.string.error_empty_password)
         else null
   }
}