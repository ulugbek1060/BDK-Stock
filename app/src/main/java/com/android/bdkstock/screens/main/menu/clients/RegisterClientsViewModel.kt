package com.android.bdkstock.screens.main.menu.clients

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.clients.ClientsRepository
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val displayUser = RegisterClientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .displayUser

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _navigateToDisplayFrag = MutableLiveEvent<Pair<Boolean, ClientEntity>>()
   val navigateToDisplay = _navigateToDisplayFrag.liveData()

   fun registerClients(
      fullName: String,
      phoneNumber: String,
      address: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val clientEntity = clientsRepository.registerClient(
            fullName, phoneNumber, address
         )
         successAndNavigateToDisplay(clientEntity)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun successAndNavigateToDisplay(clientEntity: ClientEntity) {
      _navigateToDisplayFrag.publishEvent(displayUser to clientEntity)
   }

   private fun showProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = true
      )
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = false
      )
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyFullName = e.field == Field.FULL_NAME,
         emptyPhoneNumber = e.field == Field.PHONE_NUMBER,
         emptyAddress = e.field == Field.ADDRESS,
      )
   }


   data class State(
      val isInProgress: Boolean = false,
      val emptyFullName: Boolean = false,
      val emptyPhoneNumber: Boolean = false,
      val emptyAddress: Boolean = false
   ) {

      fun getFullNameErrorMessage(context: Context) =
         if (emptyFullName) context.getString(R.string.error_empty_name)
         else null

      fun getPhoneNumberErrorMessage(context: Context) =
         if (emptyPhoneNumber) context.getString(R.string.error_empty_phone_number)
         else null

      fun getAddressErrorMessage(context: Context) =
         if (emptyAddress) context.getString(R.string.error_empty_address)
         else null
   }
}