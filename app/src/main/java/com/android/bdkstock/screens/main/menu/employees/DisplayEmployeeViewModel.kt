package com.android.bdkstock.screens.main.menu.employees

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.features.JobTitle
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.employees.EmployeeRepository
import com.android.model.utils.*
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

   private val _showMessages = MutableLiveEvent<String>()
   val showMessages = _showMessages.liveData()

   private val _clearPassword = MutableUnitLiveEvent()
   val clearPassword = _clearPassword.liveData()

   private val _jobTitle = MutableLiveData<JobTitle>()
   val jobTitle = _jobTitle.liveData()

   private val _employee = DisplayEmployeeFragmentArgs.fromSavedStateHandle(savedStateHandle)

   private val _employeeEntity = MutableLiveData(_employee.employeeEntity)
   val employeeEntity = _employeeEntity.liveData()

   init {
      _jobTitle.value = getInitialJobTitle()
   }

   fun updateEmployee(
      newFirstname: String,
      newLastname: String,
      newAddress: String,
      newPhoneNumber: String,
      newPassword: String,
      newPasswordConfirm: String
   ) = viewModelScope.safeLaunch {
      if (changesState()) {
         showProgress()
         try {
            val message = employeesRepository.updateEmployee(
               id = _employeeEntity.requireValue().id,
               firstname = newFirstname,
               lastname = newLastname,
               phoneNumber = newPhoneNumber,
               address = newAddress,
               password = newPassword,
               confirmPassword = newPasswordConfirm,
               jobId = getJobId()
            )
            successResultAndRestoreState(message)
         } catch (e: EmptyFieldException) {
            publishEmptyFieldError(e)
         } finally {
            hideProgress()
         }
      }
   }

   fun toggleChanges() {
      val changes = _state.value?.isChangesEnable ?: false

      // if editing is canceled set old values
      if (changes) _employeeEntity.value = _employee.employeeEntity // for canceling changes
      if (changes) _jobTitle.value = getInitialJobTitle() // for canceling changes

      _state.value = State(isChangesEnable = !changes)
   }

   fun setJobTitle(jobTitle: JobTitle) {
      _jobTitle.value = jobTitle
   }

   private fun getInitialJobTitle(): JobTitle {
      val job = _employeeEntity.requireValue().jobTitle
      var jobTitle: JobTitle = JobTitle(1, "Admin")
      JobTitle.getJobs().forEach {
         if (it.jobName == job) {
            jobTitle = it
            return@forEach
         }
      }
      return jobTitle
   }

   private fun changesState(): Boolean = _state.requireValue().isChangesEnable

   private fun getJobId() = _jobTitle.requireValue().jobId

   private fun successResultAndRestoreState(message: String) {
      publishMessage(message)
      _state.postValue(State())
      _clearPassword.publishEvent()
   }

   private fun publishEmptyFieldError(e: EmptyFieldException) {
      _state.value = _state.value?.copy(
         emptyFirstnameError = e.field == Field.Firstname,
         emptyLastnameError = e.field == Field.Lastname,
         emptyAddressError = e.field == Field.Address,
         emptyPhoneNumberError = e.field == Field.PhoneNumber,
         emptyPasswordError = e.field == Field.Password,
         passwordMismatch = e.field == Field.MismatchPasswordFields
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
      val isChangesEnable: Boolean = false,
      val isProgressActive: Boolean = false
   ) {
      fun getToggleButtonColor(context: Context): Int {
         return if (isChangesEnable) context.getColor(R.color.red)
         else context.getColor(R.color.blue)
      }

      fun getToggleButtonText(context: Context): String {
         return if (isChangesEnable) context.getString(R.string.cancel)
         else context.getString(R.string.edit)
      }

      fun getButtonSaveColor(context: Context): Int {
         return if (isChangesEnable) context.getColor(R.color.blue)
         else context.getColor(R.color.grey)
      }
   }
}