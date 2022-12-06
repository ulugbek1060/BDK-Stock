package com.android.bdkstock.screens.main.menu.drivers

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentRegisterDriverBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDriverFragment :
   BaseFragment<RegisterDriverViewModel, FragmentRegisterDriverBinding>() {

   override val viewModel by viewModels<RegisterDriverViewModel>()
   override fun getViewBinding() = FragmentRegisterDriverBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()

      observeVehicles()
      observeSelectedVehicle()

      navigateToDetailFrag()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeVehicles() {
      viewModel.vehicles.observe(viewLifecycleOwner) {
         val list = it ?: emptyList()
         val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)

         binding.autoCompleteVehicle.setAdapter(adapter)

         binding.autoCompleteVehicle.setOnItemClickListener { _, _, position, _ ->
            viewModel.setVehicleId(list[position])
         }
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutPhoneNumber.isEnabled = !state.isInProgress
         binding.inputLayoutVehicle.isEnabled = !state.isInProgress
         binding.inputLayoutRegNumber.isEnabled = !state.isInProgress

         binding.buttonSave.isVisible = !state.isInProgress
         binding.lottiProgress.isVisible = state.isInProgress

         binding.inputLayoutName.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberErrorMessage(requireContext())
         binding.inputLayoutVehicle.error = state.getVehicleErrorMessage(requireContext())
         binding.inputLayoutRegNumber.error = state.getRegNumberErrorMessage(requireContext())
      }
   }

   private fun saveOnClick() {
      viewModel.registerDriver(
         fullName = binding.inputName.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         regNumber = binding.inputRegNumber.text.toString()
      )
   }

   private fun observeSelectedVehicle() {
      viewModel.selectedVehicle.observe(viewLifecycleOwner) {
         binding.autoCompleteVehicle.setText(it.name, false)
      }
   }

   private fun navigateToDetailFrag() {
      viewModel.navigateToDisplayFrag.observeEvent(viewLifecycleOwner) { (display, driver) ->
         findTopNavController().popBackStack()
         if (display) findTopNavController().navigate(
            ActionsFragmentDirections
               .actionActionsFragmentToDisplayDriverFragment(
                  driver
               )
         )
      }
   }
}