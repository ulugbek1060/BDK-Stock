package com.android.bdkstock.screens.main.menu.employees

import android.content.Context
import androidx.lifecycle.MutableLiveData
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
class RegisterEmployeeViewModel @Inject constructor(
   private val employeeRepository: EmployeeRepository,
   private val jobRepository: JobRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _jobsList = MutableLiveData<List<JobEntity>>(emptyList())
   val jobsList = _jobsList.liveData()

   private val _navigate = MutableLiveEvent<EmployeeEntity>()
   val navigate = _navigate.liveData()

   private var jobId: Int? = null

   init {
      viewModelScope.safeLaunch {
         jobRepository.getJobs().collectLatest { result ->
            if (result is Success) _jobsList.value = result.value ?: emptyList()
         }
      }
   }

   fun registerEmployee(
      firstname: String,
      lastname: String,
      address: String,
      phoneNumber: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val entity = employeeRepository.registerEmployee(
            firstname = firstname,
            lastname = lastname,
            address = address,
            phoneNumber = phoneNumber,
            jobId = jobId
         )
         successEndShowMessage(entity)
      } catch (e: EmptyFieldException) {
         publishEmptyFields(e)
      } finally {
         hideProgress()
      }
   }


   fun setJobTitle(job: JobEntity) {
      jobId = job.id.toInt()
   }

   private fun successEndShowMessage(entity: EmployeeEntity) {
      _state.postValue(State(disableFields = true))

      //navigate to display employee
      _navigate.publishEvent(entity)
   }

   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyFirstname = e.field == Field.FIRSTNAME,
         emptyLastname = e.field == Field.FIRSTNAME,
         emptyAddress = e.field == Field.FIRSTNAME,
         emptyPhoneNumber = e.field == Field.FIRSTNAME,
      )
   }

   private fun showProgress() {
      _state.value = _state.requireValue().copy(isProgressActive = true)
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(isProgressActive = false)
   }

   data class State(
      val isProgressActive: Boolean = false,
      val emptyFirstname: Boolean = false,
      val emptyLastname: Boolean = false,
      val emptyAddress: Boolean = false,
      val emptyPhoneNumber: Boolean = false,
      var disableFields: Boolean = false
   ) {
      fun getNameError(context: Context) =
         if (emptyFirstname) context.getString(R.string.error_empty_name)
         else null

      fun getSurnameError(context: Context) =
         if (emptyLastname) context.getString(R.string.error_empty_surname)
         else null

      fun getAddressError(context: Context) =
         if (emptyAddress) context.getString(R.string.error_empty_address)
         else null

      fun getPhoneNumberError(context: Context) =
         if (emptyPhoneNumber) context.getString(R.string.error_empty_phone_number)
         else null

   }
}