package com.android.bdkstock.screens.main.menu.employees

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentRegisterEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.features.JobTitle
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
      observeMessages()

      setupJobTitle()

      binding.buttonSave.setOnClickListener { saveEmployeeAction() }
   }

   private fun observeMessages() {
      viewModel.showMessages.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun setupJobTitle() {
      val jobsList = JobTitle.getJobs()

      viewModel.setJobTitle(jobsList[2])

      viewModel.jobTitle.observe(viewLifecycleOwner) {
         binding.autoCompleteJobTitle.setText(it.jobName, false)
      }

      val adapter = ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, jobsList)
      binding.autoCompleteJobTitle.setAdapter(adapter)

      binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
         viewModel.setJobTitle(jobsList[position])
      }
   }

   private fun saveEmployeeAction() {
      viewModel.registerEmployee(
         firstname = binding.inputName.text.toString(),
         lastname = binding.inputSurname.text.toString(),
         address = binding.inputAddress.text.toString(),
         phoneNumber = binding.inputPhoneNumber.text.toString()
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled =
            !state.disableFields || !state.isProgressActive
         binding.inputLayoutSurname.isEnabled =
            !state.disableFields || !state.isProgressActive
         binding.inputLayoutAddress.isEnabled =
            !state.disableFields || !state.isProgressActive
         binding.inputLayoutPhoneNumber.isEnabled =
            !state.disableFields || !state.isProgressActive
         binding.inputLayoutJobTitle.isEnabled =
            !state.disableFields || !state.isProgressActive

         binding.lottiProgress.isVisible = state.isProgressActive
         binding.buttonSave.isVisible = !state.isProgressActive

         binding.inputLayoutName.error = getNameError(state.emptyFirstname)
         binding.inputLayoutSurname.error = getSurnameError(state.emptyLastname)
         binding.inputLayoutAddress.error = getAddressError(state.emptyAddress)
         binding.inputLayoutPhoneNumber.error = getPhoneNumberError(state.emptyPhoneNumber)
      }
   }

   private fun getNameError(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_name)
      else null

   private fun getSurnameError(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_surname)
      else null

   private fun getAddressError(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_address)
      else null

   private fun getPhoneNumberError(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_phone_number)
      else null
}