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
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDriverFragment : BaseFragment(R.layout.fragment_register_driver) {

   override val viewModel by viewModels<RegisterDriverViewModel>()
   private lateinit var binding: FragmentRegisterDriverBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentRegisterDriverBinding.bind(view)

      observeState()

      observeVehicles()
      observeSelectedVehicle()

      navigateToDetailFrag()

      binding.buttonSave.setOnClickListener { registerDriver() }
   }

   private fun observeVehicles() {
      viewModel.vehicles.observe(viewLifecycleOwner) {
         val list = it ?: emptyList()
         val adapter = ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, list)

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

   private fun registerDriver() {
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
      viewModel.navigateToDisplayFrag.observeEvent(viewLifecycleOwner) {
         findTopNavController().popBackStack()
         val args = ActionsFragmentDirections.actionActivityFragmentToDisplayDriverFragment(it)
         findTopNavController().navigate(args)
      }
   }
}