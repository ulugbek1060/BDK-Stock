package com.android.bdkstock.screens.main.profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.model.di.IoDispatcher
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.account.entity.PermsWithJobIdEntity
import com.android.model.repository.jobs.JobRepository
import com.android.model.repository.jobs.entity.JobEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPermissionsViewModel @Inject constructor(
   private val accountRepository: AccountRepository,
   @IoDispatcher val ioDispatcher: CoroutineDispatcher,
   private val jobRepository: JobRepository
) : ViewModel() {

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _errorMessage = MutableLiveEvent<String>()
   val errorMessage = _errorMessage.asLiveData()

   private val _navigateBack = MutableUnitLiveEvent()
   val navigateBack = _navigateBack.asLiveData()

   private val _selectedPermsOfJofTitle = MutableLiveData<PermsWithJobIdEntity?>()
   val selectedPermsOfJofTitle = _selectedPermsOfJofTitle.map {
      it?.permissions?.map { permissions ->
         permissions.id
      }
   }

   private val _newSelectedPerms = mutableSetOf<Int>()
   val newSelectedPerms: Set<Int> get() = _newSelectedPerms

   private val _jobs = MutableLiveData<List<JobEntity>>(emptyList())
   val jobs = _jobs.asLiveData()

   init {
      viewModelScope.launch(ioDispatcher) {
         jobRepository.getJobs().collectLatest { result ->
            when (result) {
               is Success -> {
                  hideProgress()
                  result.value?.let { _jobs.postValue(it) }
               }
               is Pending -> {
                  showProgress()
               }
               is Error -> {
                  hideProgress()
                  _errorMessage.publishEventOnBackground(
                     result.error.localizedMessage ?: "Unknown error!"
                  )
               }
               else -> {
                  hideProgress()
                  _errorMessage.publishEventOnBackground("Unknown error!")
               }
            }
         }
      }
   }

   fun getPermissionOfJobTitle(jobId: Int) = viewModelScope.launch(ioDispatcher) {
      accountRepository.getPermissionOfJobTitle(
         jobId = jobId
      ).collectLatest { result ->
         when (result) {
            is Success -> {
               hideProgress()
               result.value.let {
                  _selectedPermsOfJofTitle.postValue(it)
                  _newSelectedPerms.clear()
                  val perms = it.permissions.map { permId -> permId.id }
                  _newSelectedPerms.addAll(perms)
               }
            }
            is Pending -> {
               showProgress()
            }
            is Error -> {
               hideProgress()
               result.error.localizedMessage?.let {
                  _errorMessage.publishEventOnBackground(it)
               }
            }
            else -> {
               hideProgress()
               _errorMessage.publishEvent("Something went wrong!!!")
            }
         }
      }
   }

   fun updatePermissions() = viewModelScope.launch {
      showProgress()
      try {
         accountRepository.updateUsersPermissions(
            jobId = getJobId(),
            permissions = _newSelectedPerms.toList()
         )
         _navigateBack.publishEvent()
      } catch (e: ConnectionException) {
         _errorMessage.publishEvent(e.localizedMessage ?: "Undefined Message from Backend")
      } catch (e: BackendException) {
         _errorMessage.publishEvent(e.localizedMessage ?: "Undefined Message from Backend")
      } catch (e: AuthException) {
         _errorMessage.publishEvent(e.localizedMessage ?: "Undefined Message from Backend")
      } catch (e: Exception) {
         _errorMessage.publishEvent(e.localizedMessage ?: "Undefined Message from Backend")
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   fun setSelectedPerms(permissionId: Int?) {
      if (permissionId == null) return
      if (_newSelectedPerms.contains(permissionId)) {
         _newSelectedPerms -= permissionId
      } else {
         _newSelectedPerms += permissionId
      }
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         isJobIdNull = e.field == Field.JOB
      )
   }

   private fun getJobId() = _selectedPermsOfJofTitle.requireValue()?.jobId

   private fun showProgress() {
      _state.postValue(
         _state.requireValue().copy(
            isInProgress = true
         )
      )
   }

   private fun hideProgress() {
      _state.postValue(
         _state.requireValue().copy(
            isInProgress = false
         )
      )
   }

   data class State(
      val isInProgress: Boolean = false,
      val isJobIdNull: Boolean = false
   ) {

      fun getJobErrorMessage(context: Context) =
         if (isJobIdNull) context.getString(R.string.empty_jobId)
         else null
   }
}