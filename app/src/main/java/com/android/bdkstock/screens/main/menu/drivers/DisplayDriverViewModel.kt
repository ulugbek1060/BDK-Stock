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
class DisplayDriverViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentDriver = DisplayDriverFragmentArgs
      .fromSavedStateHandle(savedStateHandle)

   private val _driver = MutableLiveData<DriverEntity>()
   val driver = _driver.liveData()

   private val _vehicles = MutableLiveData<List<VehicleModelEntity>?>(emptyList())
   val vehicles = _vehicles.liveData()

   private val _selectedVehicle = MutableLiveData(VehicleModelEntity())
   val selectedVehicle = _selectedVehicle.liveData()

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _showSuggestionDialog = MutableUnitLiveEvent()
   val showSuggestionDialog = _showSuggestionDialog.liveData()

   private val _showSuccessMessage = MutableLiveEvent<String>()
   val showSuccessMessage = _showSuccessMessage.liveData()

   init {
      getInitialData()
      viewModelScope.safeLaunch {
         driversRepository.getVehicleModelsList().collectLatest { result ->
            if (result is Success) _vehicles.value = result.value
            if (result is Error) _vehicles.value =
               listOf(VehicleModelEntity(id = null, name = "Some error occurred!"))
         }
      }
   }

   private fun getInitialData() = try {
      val entity = _currentDriver.driverEntity
      val phoneNumber = formatPhoneNumber(entity.phoneNumber)
      val vehicle = entity.vehicle

      // set driver
      _driver.value = entity.copy(
         phoneNumber = phoneNumber
      )

      // set selected vehicle
      _selectedVehicle.value = vehicle
   } catch (e: Exception) {
      e.printStackTrace()
   }

   fun updateDriver(
      fullName: String,
      phoneNumber: String,
      regNumber: String
   ) = viewModelScope.safeLaunch {
      if (changeableState()) {
         showProgress()
         try {

            val message = driversRepository.updateDriver(
               driverId = driverId,
               fullName = fullName,
               phoneNumber = phoneNumber,
               autoModelId = vehicleId(),
               regNumber = regNumber,
            )

            // show success message
            showSuccessMessage(message)

            // update driver fields
            setDriverFields(fullName, phoneNumber, regNumber)

         } catch (e: EmptyFieldException) {
            publishEmptyFields(e)
         } finally {
            hideProgress()
         }
      }
   }

   private fun setDriverFields(fullName: String, phoneNumber: String, regNumber: String) {
      _driver.value = _driver.requireValue().copy(
         driverFullName = fullName,
         phoneNumber = phoneNumber,
         autoRegNumber = regNumber,
         vehicle = _selectedVehicle.requireValue()
      )
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
      getInitialData()
   }

   fun setVehicle(vehicle: VehicleModelEntity) {
      _selectedVehicle.value = vehicle
   }

   private fun showSuccessMessage(message: String) {
      _showSuccessMessage.publishEvent(message)
      _state.value = State()
   }


   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyNameError = e.field == Field.FulLName,
         emptyPhoneNumberError = e.field == Field.PhoneNumber,
         emptyVehicleError = e.field == Field.VehicleModel,
         emptyRegNumberError = e.field == Field.RegNumber
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

   private fun changeableState() = _state.requireValue().isChangeableEnable

   private val driverId = _driver.requireValue().id

   private fun vehicleId() = _selectedVehicle.requireValue().id

   data class State(
      val isChangeableEnable: Boolean = false,
      val isInProgress: Boolean = false,
      val emptyNameError: Boolean = false,
      val emptyPhoneNumberError: Boolean = false,
      val emptyVehicleError: Boolean = false,
      val emptyRegNumberError: Boolean = false
   ) {

      fun getToggleButtonColor(context: Context) =
         if (isChangeableEnable) context.getColor(R.color.red)
         else context.getColor(R.color.blue)

      fun getToggleButtonText(context: Context) =
         if (isChangeableEnable) context.getString(R.string.cancel)
         else context.getString(R.string.edit)

      fun getButtonSaveColor(context: Context) =
         if (isChangeableEnable) context.getColor(R.color.blue)
         else context.getColor(R.color.grey)

      fun getNameErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_name) else null

      fun getPhoneNumberErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_phone_number) else null

      fun getVehicleErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_vehicle) else null

      fun getRegNumberErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_reg_number) else null
   }
}