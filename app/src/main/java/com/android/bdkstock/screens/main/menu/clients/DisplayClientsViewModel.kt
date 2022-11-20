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
class DisplayClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentClient = DisplayClientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _clientEntity = MutableLiveData<ClientEntity>()
   val clientEntity = _clientEntity.liveData()

   private val _showSuggestionDialog = MutableUnitLiveEvent()
   val showSuggestionDialog = _showSuggestionDialog.liveData()

   private val _showSuccessMessage = MutableLiveEvent<String>()
   val showSuccessMessage = _showSuccessMessage.liveData()

   init {
      getInitialValue()
   }

   private fun getInitialValue() = try {
      val entity = _currentClient.clientEntity
      val phoneNumber = formatPhoneNumber(entity.phoneNumber)
      _clientEntity.value = entity.copy(
         phoneNumber = phoneNumber
      )
   } catch (e: Exception) {
      e.printStackTrace()
   }

   fun updateClient(
      fullName: String,
      phoneNumber: String,
      address: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val message = clientsRepository.updateClient(
            getClientId(), fullName, phoneNumber, address
         )

         // show success message
         showSuccessMessageAndRefreshState(message)

         // update driver fields
         setDriverData(fullName, phoneNumber, address)

      } catch (e: EmptyFieldException) {
         publishEmptyFields(e)
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

   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyFullName = e.field == Field.FulLName,
         emptyPhoneNumber = e.field == Field.PhoneNumber,
         emptyAddress = e.field == Field.Address,
      )
   }

   private fun setDriverData(fullName: String, phoneNumber: String, address: String) {
      val formattedNumber = formatPhoneNumber(phoneNumber)
      _clientEntity.value = _clientEntity.requireValue().copy(
         fullName = fullName, phoneNumber = formattedNumber, address = address
      )
   }

   private fun showSuccessMessageAndRefreshState(message: String) {
      _showSuccessMessage.publishEvent(message)
      _state.value = State()
   }

   private fun getClientId() = _clientEntity.requireValue().clientId

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

   private fun changeableState() = _state.requireValue().isChangeableEnable

   data class State(
      val isChangeableEnable: Boolean = false,
      val isInProgress: Boolean = false,
      val emptyFullName: Boolean = false,
      val emptyPhoneNumber: Boolean = false,
      val emptyAddress: Boolean = false
   ) {

      fun getToggleButtonColor(context: Context) =
         if (isChangeableEnable) context.getColor(R.color.red)
         else context.getColor(R.color.blue)

      fun getToggleButtonText(context: Context) =
         if (isChangeableEnable) context.getString(R.string.cancel)
         else context.getString(R.string.edit)

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