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
class EditClientViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentClient = EditClientFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .clientEntity

   private val _client = MutableLiveData<ClientEntity>()
   val client = _client.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _navigateBack = MutableLiveEvent<ClientEntity>()
   val navigateBack = _navigateBack.asLiveData()

   init {
      _client.value = _currentClient
   }

   fun updateClient(
      fullName: String,
      phoneNumber: String,
      address: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         clientsRepository.updateClient(
            clientId = getClientId(),
            fullName = fullName,
            phoneNumber = formatPhoneNumber(phoneNumber),
            address = address
         )
         _client.value = _client.requireValue().copy(
            fullName = fullName,
            phoneNumber = phoneNumber,
            address = address
         )
         _navigateBack.publishEvent(_client.requireValue())
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyFullName = e.field == Field.FULL_NAME,
         emptyPhoneNumber = e.field == Field.PHONE_NUMBER,
         emptyAddress = e.field == Field.ADDRESS,
      )
   }

   private fun getClientId() = _client.requireValue().clientId

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