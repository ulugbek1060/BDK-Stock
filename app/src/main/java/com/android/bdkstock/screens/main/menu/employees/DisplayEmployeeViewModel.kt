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

   private val _showChangesDialog = MutableUnitLiveEvent()
   val showChangesDialog = _showChangesDialog.liveData()

   private val _jobsEntity = MutableLiveData<Results<List<JobEntity>?>>(Pending())
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
         jobsRepository.getJobs().collectLatest {
            _jobsEntity.value = it
         }
      }
      getInitialDateOfEmployee()
   }

   /**
    * 1. initial user data
    */
   private fun getInitialDateOfEmployee() = try {
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
      if (isChangeable()) {
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
   }

   fun setChangeableState(checkState: Boolean) {
      if (checkState) {
         showDialogEvent()
      } else {
         _state.value = State(isChangesEnable = false)
         getInitialDateOfEmployee()
      }
   }

   /**
    * to change job title
    */
   fun setJobEntity(job: JobEntity) {
      _employeeEntity.value = _employeeEntity.requireValue().copy(
         job = job
      )
   }

   fun enableChangeableState() {
      _state.value = _state.requireValue().copy(
         isChangesEnable = true
      )
   }

   /**
    * to know change state is active or not
    */
   private fun isChangeable(): Boolean = _state.requireValue().isChangesEnable

   private fun showDialogEvent() = _showChangesDialog.publishEvent()

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