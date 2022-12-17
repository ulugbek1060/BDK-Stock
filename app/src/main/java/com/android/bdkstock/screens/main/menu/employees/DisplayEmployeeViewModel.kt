package com.android.bdkstock.screens.main.menu.employees

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayEmployeeViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : BaseViewModel() {

   private val _currentEmployee = DisplayEmployeeFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .employeeEntity

   private val _employee = MutableLiveData<EmployeeEntity>()
   val employee = _employee.liveData()


   private val _navigateToEdit = MutableLiveEvent<EmployeeEntity>()
   val navigateToEdit = _navigateToEdit.liveData()

   fun navigateToEdit() {
      _navigateToEdit.publishEvent(_employee.requireValue())
   }

   init {
      val employee = _currentEmployee
      _employee.value = employee.copy(
         phoneNumber = formatPhoneNumber(employee.phoneNumber)
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

   fun setUpdatedEntity(employee: EmployeeEntity?) {
      if (employee == null) return
      _employee.value = employee!!
   }
}