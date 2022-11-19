package com.android.bdkstock.screens.main.menu.drivers

import android.content.Context
import androidx.lifecycle.MutableLiveData
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
   private val accountRepository: AccountRepository,
   private val driversRepository: DriversRepository
) : BaseViewModel(accountRepository) {

   private val _vehicles = MutableLiveData<List<VehicleModelEntity>?>(emptyList())
   val vehicles = _vehicles.liveData()

   private val _selectedVehicle = MutableLiveData(VehicleModelEntity())
   val selectedVehicle = _selectedVehicle.liveData()

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _navigateToDisplayFrag = MutableLiveEvent<DriverEntity>()
   val navigateToDisplayFrag = _navigateToDisplayFrag.liveData()

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
         publishEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun navigateSuccessfully(driver: DriverEntity) {
      _navigateToDisplayFrag.publishEvent(driver)
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

   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyNameError = e.field == Field.FulLName,
         emptyPhoneNumberError = e.field == Field.PhoneNumber,
         emptyVehicleError = e.field == Field.VehicleModel,
         emptyRegNumberError = e.field == Field.RegNumber
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