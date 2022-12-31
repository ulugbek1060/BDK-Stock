package com.android.bdkstock.screens.main.menu.drivers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.drivers.DriversRepository
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.drivers.entity.VehicleModelEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class EditDriverViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentDriver = EditDriverFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .driverEntity

   private val _vehicleModels = MutableLiveData<List<VehicleModelEntity>>()
   val vehicleModels = _vehicleModels.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _driver = MutableLiveData<DriverEntity>()
   val driver = _driver.asLiveData()

   private val _navigateBack = MutableLiveEvent<DriverEntity>()
   val navigateBack = _navigateBack.asLiveData()

   init {
      _driver.value = _currentDriver

      viewModelScope.safeLaunch {
         driversRepository.getVehicleModelsList().collectLatest {
            val list = it.getValueOrNull() ?: emptyList()
            _vehicleModels.value = list
         }
      }
   }

   fun updateDriver(
      fullName: String,
      phoneNumber: String,
      regNumber: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         driversRepository.updateDriver(
            driverId = driverId,
            fullName = fullName,
            phoneNumber = phoneNumber,
            autoModelId = vehicleId(),
            regNumber = regNumber,
         )
         _driver.value = _driver.requireValue().copy(
            id = driverId,
            driverFullName = fullName,
            phoneNumber = formatPhoneNumber(phoneNumber),
            autoRegNumber = regNumber,
         )
         _navigateBack.publishEvent(_driver.requireValue())
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   fun setVehicle(vehicle: VehicleModelEntity) {
      _driver.value = _driver.requireValue().copy(
         vehicle = vehicle
      )
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyNameError = e.field == Field.FULL_NAME,
         emptyPhoneNumberError = e.field == Field.PHONE_NUMBER,
         emptyVehicleError = e.field == Field.VEHICLE_MODEL,
         emptyRegNumberError = e.field == Field.REG_NUMBER
      )
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

   private val driverId = _driver.requireValue().id

   private fun vehicleId() = _driver.requireValue().vehicle.id

   data class State(
      val isInProgress: Boolean = false,
      val emptyNameError: Boolean = false,
      val emptyPhoneNumberError: Boolean = false,
      val emptyVehicleError: Boolean = false,
      val emptyRegNumberError: Boolean = false
   ) {

      fun getNameErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_name)
         else null

      fun getPhoneNumberErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_phone_number)
         else null

      fun getVehicleErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_vehicle)
         else null

      fun getRegNumberErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_reg_number)
         else null
   }
}