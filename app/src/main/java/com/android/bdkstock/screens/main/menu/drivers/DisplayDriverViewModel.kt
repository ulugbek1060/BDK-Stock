package com.android.bdkstock.screens.main.menu.drivers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.repository.drivers.DriversRepository
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.drivers.entity.VehicleModelEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DisplayDriverViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : BaseViewModel() {

   private val _currentDriver = DisplayDriverFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .driverEntity

   private val _driver = MutableLiveData<DriverEntity>()
   val driver = _driver.liveData()


   private val _navigateToEdit = MutableLiveEvent<DriverEntity>()
   val navigateToEdit = _navigateToEdit.liveData()

   init {
      val driver = _currentDriver
      _driver.value = driver.copy(
         phoneNumber = formatPhoneNumber(driver.phoneNumber)
      )
   }

   fun navigateToEdit() {
      _navigateToEdit.publishEvent(_driver.requireValue())
   }

   fun setUpdatedEntity(driverEntity: DriverEntity?) {
      if (driverEntity == null) return
      _driver.value = driverEntity!!
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