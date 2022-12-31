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
class RegisterDriverViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val displayDriver = RegisterDriverFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .displayDriver

   private val _vehicles = MutableLiveData<List<VehicleModelEntity>?>(emptyList())
   val vehicles = _vehicles.asLiveData()

   private val _selectedVehicle = MutableLiveData(VehicleModelEntity())
   val selectedVehicle = _selectedVehicle.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _navigateToDisplayFrag = MutableLiveEvent<Pair<Boolean, DriverEntity>>()
   val navigateToDisplayFrag = _navigateToDisplayFrag.asLiveData()

   init {
      viewModelScope.safeLaunch {
         driversRepository.getVehicleModelsList().collectLatest { result ->
            if (result is Success) _vehicles.value = result.value
         }
      }
   }

   fun registerDriver(
      fullName: String,
      phoneNumber: String,
      regNumber: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val driver = driversRepository.registerDriver(
            fullName = fullName,
            phoneNumber = phoneNumber,
            vehicleModelId = getVehicleValue(),
            regNumber = regNumber
         )
         navigateSuccessfully(driver)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun navigateSuccessfully(driver: DriverEntity) {
      _navigateToDisplayFrag.publishEvent(displayDriver to driver)
   }

   fun setVehicleId(vehicleId: VehicleModelEntity) {
      _selectedVehicle.value = vehicleId
   }

   private fun getVehicleValue() = _selectedVehicle.requireValue().id

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
         emptyNameError = e.field == Field.FULL_NAME,
         emptyPhoneNumberError = e.field == Field.PHONE_NUMBER,
         emptyVehicleError = e.field == Field.VEHICLE_MODEL,
         emptyRegNumberError = e.field == Field.REG_NUMBER
      )
   }

   data class State(
      val isInProgress: Boolean = false,
      val emptyNameError: Boolean = false,
      val emptyPhoneNumberError: Boolean = false,
      val emptyVehicleError: Boolean = false,
      val emptyRegNumberError: Boolean = false
   ) {
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