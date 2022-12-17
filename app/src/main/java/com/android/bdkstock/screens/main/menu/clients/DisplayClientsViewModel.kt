package com.android.bdkstock.screens.main.menu.clients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayClientsViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : BaseViewModel() {

   private val _currentClient = DisplayClientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .clientEntity

   private val _client = MutableLiveData<ClientEntity>()
   val client = _client.liveData()

   private val _navigateToEdit = MutableLiveEvent<ClientEntity>()
   val navigateToEdit = _navigateToEdit.liveData()

   init {
      val client = _currentClient
      _client.value = client.copy(
         phoneNumber = formatPhoneNumber(client.phoneNumber)
      )
   }

   fun navigateToEdit() {
      _navigateToEdit.publishEvent(_client.requireValue())
   }

   fun setUpdatedEntity(clientEntity: ClientEntity?) {
      if (clientEntity == null) return
      _client.value = clientEntity!!
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


}