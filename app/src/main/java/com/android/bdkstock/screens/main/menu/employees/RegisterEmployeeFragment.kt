package com.android.bdkstock.screens.main.menu.employees

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentRegisterEmployeeBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterEmployeeFragment : BaseFragment(R.layout.fragment_register_employee) {

   private lateinit var binding: FragmentRegisterEmployeeBinding
   override val viewModel by viewModels<RegisterEmployeeViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentRegisterEmployeeBinding.bind(view)

      observeState()
      observeNavigation()

      fetchJobTitle()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeNavigation() {
      viewModel.navigate.observeEvent(viewLifecycleOwner) {
         findTopNavController().popBackStack()
         val args = ActionsFragmentDirections.actionActionsFragmentToDisplayEmployeeFragment(it)
         findTopNavController().navigate(args)
      }
   }

   private fun fetchJobTitle() {
      viewModel.jobsList.observe(viewLifecycleOwner) { list ->
         binding.autoCompleteJobTitle.setText(null, false)

         val adapter = ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, list)
         binding.autoCompleteJobTitle.setAdapter(adapter)

         binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
            viewModel.setJobTitle(list[position])
         }
      }
   }

   private fun saveOnClick() {
      viewModel.registerEmployee(
         firstname = binding.inputName.text.toString(),
         lastname = binding.inputSurname.text.toString(),
         address = binding.inputAddress.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}"
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled = !state.isProgressActive
         binding.inputLayoutSurname.isEnabled = !state.isProgressActive
         binding.inputLayoutAddress.isEnabled = !state.isProgressActive
         binding.inputLayoutPhoneNumber.isEnabled = !state.isProgressActive
         binding.inputLayoutJobTitle.isEnabled = !state.isProgressActive

         binding.lottiProgress.isVisible = state.isProgressActive
         binding.buttonSave.isVisible = !state.isProgressActive

         binding.inputLayoutName.error = state.getNameError(requireContext())
         binding.inputLayoutSurname.error = state.getSurnameError(requireContext())
         binding.inputLayoutAddress.error = state.getAddressError(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberError(requireContext())
      }
   }
}