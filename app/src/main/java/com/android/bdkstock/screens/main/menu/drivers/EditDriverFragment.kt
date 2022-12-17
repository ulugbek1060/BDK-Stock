package com.android.bdkstock.screens.main.menu.drivers

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentEdtiDriverBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDriverFragment : BaseFragment<EditDriverViewModel, FragmentEdtiDriverBinding>() {

   override val viewModel: EditDriverViewModel by viewModels()
   override fun getViewBinding() = FragmentEdtiDriverBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeDriver()
      observeState()
      observeVehicles()
      observeNavigateBack()

      binding.buttonSave.setOnClickListener { saveOnClick() }
//      binding.buttonDelete.setOnClickListener { deleteOnClick() }
   }

   private fun observeNavigateBack() {
      viewModel.navigateBack.observeEvent(viewLifecycleOwner) {
         setFragmentResult(DRIVER_ENTITY_KEY, bundleOf(DRIVER_ENTITY_BUNDLE_KEY to it))
         findTopNavController().popBackStack()
      }
   }

   private fun observeDriver() {
      viewModel.driver.observe(viewLifecycleOwner) { driver ->
         with(binding) {
            inputName.setText(driver.driverFullName)
            inputPhoneNumber.setText(driver.phoneNumber)
            autoCompleteVehicle.setText(driver.vehicle.name, false)
            inputRegNumber.setText(driver.autoRegNumber)
         }
      }
   }

   private fun observeVehicles() {
      viewModel.vehicleModels.observe(viewLifecycleOwner) { list ->
         val adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, list)
         binding.autoCompleteVehicle.setAdapter(adapter)

         binding.autoCompleteVehicle.setOnItemClickListener { _, _, position, _ ->
            viewModel.setVehicle(list[position])
         }
      }
   }

   private fun saveOnClick() {
      viewModel.updateDriver(
         fullName = binding.inputName.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         regNumber = binding.inputRegNumber.text.toString()
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutPhoneNumber.isEnabled = !state.isInProgress
         binding.inputLayoutRegNumber.isEnabled = !state.isInProgress
         binding.inputLayoutVehicle.isEnabled = !state.isInProgress

         binding.inputLayoutName.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberErrorMessage(requireContext())
         binding.inputLayoutRegNumber.error = state.getRegNumberErrorMessage(requireContext())
         binding.inputLayoutVehicle.error = state.getVehicleErrorMessage(requireContext())

         binding.buttonSave.isVisible = !state.isInProgress
         binding.progressbar.isVisible = state.isInProgress
      }
   }

   private companion object{
      const val DRIVER_ENTITY_KEY = "driver_entity"
      const val DRIVER_ENTITY_BUNDLE_KEY = "driver_entity_bundle"
   }
}