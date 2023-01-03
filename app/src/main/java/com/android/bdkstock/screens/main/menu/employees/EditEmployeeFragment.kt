package com.android.bdkstock.screens.main.menu.employees

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentEditEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditEmployeeFragment : BaseFragment<EditEmployeeViewModel, FragmentEditEmployeeBinding>() {

   override val viewModel: EditEmployeeViewModel by viewModels()
   override fun getViewBinding() = FragmentEditEmployeeBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeEmployee()
      observeNavigateBack()

      getJobs()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.buttonDelete.setOnClickListener { deleteOnClick() }
   }

   private fun deleteOnClick() {

   }

   private fun getJobs() = lifecycleScopeStarted {
      viewModel.jobs.observe(viewLifecycleOwner){ list ->
         val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
         binding.autoCompleteJobTitle.setAdapter(adapter)

         binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
            viewModel.setJobEntity(list[position])
         }
      }
   }

   private fun saveOnClick() {
      viewModel.updateEmployee(
         newFirstname = binding.inputName.text.toString(),
         newLastname = binding.inputSurname.text.toString(),
         newAddress = binding.inputAddress.text.toString(),
         newPhoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         newPassword = binding.inputPassword.text.toString(),
         newPasswordConfirm = binding.inputPasswordConfirm.text.toString()
      )
   }

   private fun observeNavigateBack() {
      viewModel.navigateBack.observeEvent(viewLifecycleOwner) {
         setFragmentResult(EMPLOYEE_ENTITY_KEY, bundleOf(EMPLOYEE_ENTITY_BUNDLE_KEY to it))
         findTopNavController().popBackStack()
      }
   }

   private fun observeEmployee() {
      viewModel.employee.observe(viewLifecycleOwner) { employee ->
         binding.inputName.setText(employee.firstname)
         binding.inputSurname.setText(employee.lastname)
         binding.inputAddress.setText(employee.address)
         binding.inputPhoneNumber.setText(employee.phoneNumber)
         binding.autoCompleteJobTitle.setText(employee.job.name, false)
         binding.inputPassword.setText("")
         binding.inputPasswordConfirm.setText("")
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutSurname.isEnabled = !state.isInProgress
         binding.inputLayoutAddress.isEnabled = !state.isInProgress
         binding.inputLayoutPhoneNumber.isEnabled = !state.isInProgress
         binding.inputLayoutPassword.isEnabled = !state.isInProgress
         binding.inputLayoutJobTitle.isEnabled = !state.isInProgress
         binding.inputLayoutPasswordConfirm.isEnabled = !state.isInProgress

         binding.buttonSave.isEnabled = !state.isInProgress
         binding.buttonDelete.isEnabled = !state.isInProgress

         binding.inputLayoutName.error = state.getNameError(requireContext())
         binding.inputLayoutSurname.error = state.getSurnameError(requireContext())
         binding.inputLayoutAddress.error = state.getAddressError(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberError(requireContext())
         binding.inputLayoutPassword.error = state.getPasswordError(requireContext())
         binding.inputLayoutPasswordConfirm.error = state.getPasswordError(requireContext())

         binding.progressbar.isVisible = state.isInProgress
         binding.buttonSave.isVisible = !state.isInProgress
         binding.buttonDelete.isVisible = !state.isInProgress
      }
   }

   private companion object {
      const val EMPLOYEE_ENTITY_KEY = "client_entity"
      const val EMPLOYEE_ENTITY_BUNDLE_KEY = "client_entity_bundle"
   }
}