package com.android.bdkstock.screens.main.menu.employees

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.features.JobTitle
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.employees.EmployeeRepository
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterEmployeeViewModel @Inject constructor(
   private val employeeRepository: EmployeeRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _showMessages = MutableLiveEvent<String>()
   val showMessages = _showMessages.liveData()

   private val _jobTitle = MutableLiveData<JobTitle>()
   val jobTitle = _jobTitle.liveData()

   fun registerEmployee(
      firstname: String,
      lastname: String,
      address: String,
      phoneNumber: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val message = employeeRepository.registerEmployee(
            firstname = firstname,
            lastname = lastname,
            address = address,
            phoneNumber = phoneNumber,
            jobId = getJobId()
         )
         successEndShowMessage(message)
      } catch (e: EmptyFieldException) {
         publishEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun successEndShowMessage(message: String) {
      _showMessages.publishEvent(message)
      _state.postValue(State(disableFields = true))
   }

   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyFirstname = e.field == Field.Firstname,
         emptyLastname = e.field == Field.Firstname,
         emptyAddress = e.field == Field.Firstname,
         emptyPhoneNumber = e.field == Field.Firstname,
      )
   }

   private fun showProgress() {
      _state.value = _state.requireValue().copy(isProgressActive = true)
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(isProgressActive = false)
   }

   private fun getJobId() = _jobTitle.requireValue().jobId

   fun setJobTitle(jobTitle: JobTitle) {
      _jobTitle.value = jobTitle
   }

   data class State(
      val isProgressActive: Boolean = false,
      val emptyFirstname: Boolean = false,
      val emptyLastname: Boolean = false,
      val emptyAddress: Boolean = false,
      val emptyPhoneNumber: Boolean = false,
      var disableFields: Boolean = false
   )
}