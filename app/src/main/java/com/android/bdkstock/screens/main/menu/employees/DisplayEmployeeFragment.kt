package com.android.bdkstock.screens.main.menu.employees

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.features.JobTitle
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayEmployeeFragment : BaseFragment(R.layout.fragment_display_employee) {

   private lateinit var binding: FragmentDisplayEmployeeBinding
   override val viewModel by viewModels<DisplayEmployeeViewModel>()
   private val TAG = this.javaClass.simpleName

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayEmployeeBinding.bind(view)

      fetchEmployee()
      setupJobTitle()
      observeState()

      observeJobTitle()
      observeClearPassword()
      showMessages()

      binding.buttonSave.setOnClickListener { saveEmployeeWithChanges() }
      binding.buttonEdit.setOnClickListener { toggleChangesAction() }
      binding.buttonDelete.setOnClickListener { }

      binding.buttonCall.setOnClickListener { executeCall() }
      binding.buttonMessage.setOnClickListener { }
   }

   private fun observeClearPassword() {
      viewModel.clearPassword.observeEvent(viewLifecycleOwner) {
         binding.inputPassword.setText("")
         binding.inputPasswordConfirm.setText("")
      }
   }

   private fun setupJobTitle() {
      val jobsList = JobTitle.getJobs()

      val adapter = ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, jobsList)
      binding.autoCompleteJobTitle.setAdapter(adapter)

      binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
         viewModel.setJobTitle(jobsList[position])
      }
   }

   private fun saveEmployeeWithChanges() {
      viewModel.updateEmployee(
         newFirstname = binding.inputName.text.toString(),
         newLastname = binding.inputSurname.text.toString(),
         newAddress = binding.inputAddress.text.toString(),
         newPhoneNumber = binding.inputPhoneNumber.text.toString(),
         newPassword = binding.inputPassword.text.toString(),
         newPasswordConfirm = binding.inputPasswordConfirm.text.toString()
      )
   }

   private fun showMessages() {
      viewModel.showMessages.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun observeJobTitle() {
      viewModel.jobTitle.observe(viewLifecycleOwner) {
         binding.autoCompleteJobTitle.setText(it.jobName, false)
      }
   }

   private fun fetchEmployee() {
      viewModel.employeeEntity.observe(viewLifecycleOwner) { employee ->
         binding.inputName.setText(employee.firstname)
         binding.inputSurname.setText(employee.lastname)
         binding.inputAddress.setText(employee.address)
         binding.inputPhoneNumber.setText(employee.phoneNumber)
         binding.inputPassword.setText("")
         binding.inputPasswordConfirm.setText("")
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         // enable fields
         binding.inputLayoutName.isEnabled = state.isChangesEnable
         binding.inputLayoutSurname.isEnabled = state.isChangesEnable
         binding.inputLayoutPhoneNumber.isEnabled = state.isChangesEnable
         binding.inputLayoutAddress.isEnabled = state.isChangesEnable
         binding.inputLayoutPassword.isEnabled = state.isChangesEnable
         binding.inputLayoutPasswordConfirm.isEnabled = state.isChangesEnable
         binding.inputLayoutJobTitle.isEnabled = state.isChangesEnable

         // change buttons style
         binding.containerSaveButton.setBackgroundColor(state.getButtonSaveColor(requireContext()))
         binding.buttonEdit.setTextColor(state.getToggleButtonColor(requireContext()))
         binding.buttonEdit.setText(state.getToggleButtonText(requireContext()))

         // show errors
         binding.inputLayoutName.error = getNameError(state.emptyFirstnameError)
         binding.inputLayoutSurname.error = getSurnameError(state.emptyLastnameError)
         binding.inputLayoutAddress.error = getAddressError(state.emptyAddressError)
         binding.inputLayoutPhoneNumber.error = getPhoneNumberError(state.emptyPhoneNumberError)
         binding.inputLayoutPassword.error = getPasswordError(state.emptyPasswordError)
         binding.inputLayoutPasswordConfirm.error = getPasswordError(state.passwordMismatch)

         // show progress
         binding.containerButtons.isVisible = !state.isProgressActive
         binding.lottiProgress.isVisible = state.isProgressActive
      }
   }

   private fun toggleChangesAction() {
      viewModel.toggleChanges()
   }

   private fun executeCall() {
//      val uri = "tel:" + binding.inputPhoneNumber.text.toString()
//      val intent = Intent(Intent.ACTION_CALL)
//      intent.data = Uri.parse(uri)
//      requireActivity().startActivity(intent)
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

   private fun getPasswordError(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_password)
      else null

}