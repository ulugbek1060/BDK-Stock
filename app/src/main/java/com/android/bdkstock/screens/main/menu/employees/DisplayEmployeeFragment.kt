package com.android.bdkstock.screens.main.menu.employees

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayEmployeeFragment : BaseFragment(R.layout.fragment_display_employee) {

   private lateinit var binding: FragmentDisplayEmployeeBinding
   override val viewModel by viewModels<DisplayEmployeeViewModel>()

   private val TAG = this.javaClass.simpleName

   private val requestMessagePermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotMessagePermission
   )

   private val requestCallPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotCallPermissionResult
   )

   private fun onGotMessagePermission(granted: Boolean) {
      if (granted) {
         onMessagePermissionGranted()
      } else {
         if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
            askForUserOpenSettings()
         } else {
            Toast.makeText(
               requireContext(),
               getString(R.string.permission_denied),
               Toast.LENGTH_SHORT
            ).show()
         }
      }
   }

   private fun onGotCallPermissionResult(granted: Boolean) {
      if (granted) {
         onCallPermissionGranted()
      } else {
         if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            askForUserOpenSettings()
         } else {
            Toast.makeText(
               requireContext(),
               getString(R.string.permission_denied),
               Toast.LENGTH_SHORT
            ).show()
         }
      }
   }

   @SuppressLint("QueryPermissionsNeeded")
   private fun askForUserOpenSettings() {
      val appSettingsIntent = Intent(
         Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
         Uri.fromParts("package", requireActivity().packageName, null)
      )
      if (appSettingsIntent.resolveActivity(requireActivity().packageManager) == null) {
         Toast.makeText(requireContext(), R.string.permissions_denied_forever, Toast.LENGTH_SHORT)
            .show()
      } else {
         AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_denied)
            .setMessage(R.string.permissions_denied_forever)
            .setPositiveButton(R.string.open) { _, _ ->
               requireActivity().startActivity(appSettingsIntent)
            }
            .create()
            .show()
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayEmployeeBinding.bind(view)

      fetchEmployee()
      setupJobTitle()
      observeState()

      observeClearPassword()
      showMessages()

      observeChangesDialogEvent()

      binding.buttonSave.setOnClickListener { saveEmployeeWithChanges() }
      binding.buttonEdit.setOnCheckedChangeListener { _, isChecked -> toggleChangesAction(isChecked) }
      binding.buttonDelete.setOnClickListener { }

      binding.buttonCall.setOnClickListener {
         requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
      }

      binding.buttonMessage.setOnClickListener {
         requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
      }
   }

   private fun observeChangesDialogEvent() {
      viewModel.showChangesDialog.observeEvent(viewLifecycleOwner) {
         AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit)
            .setMessage(R.string.edit_user_details)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.enableChangeableState() }
            .create()
            .show()
      }
   }

   private fun observeClearPassword() {
      viewModel.clearPassword.observeEvent(viewLifecycleOwner) {
         binding.inputPassword.setText("")
         binding.inputPasswordConfirm.setText("")
      }
   }

   private fun setupJobTitle() = lifecycleScope.launchWhenStarted {
      viewModel.jobsEntity.observeResults(this@DisplayEmployeeFragment, binding.progressJobTitle) { jobs ->
            val list = jobs ?: emptyList()
            val adapter =
               ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, list)
            binding.autoCompleteJobTitle.setAdapter(adapter)

            binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
               viewModel.setJobEntity(list[position])
            }
         }
   }

   private fun saveEmployeeWithChanges() {
      viewModel.updateEmployee(
         newFirstname = binding.inputName.text.toString(),
         newLastname = binding.inputSurname.text.toString(),
         newAddress = binding.inputAddress.text.toString(),
         newPhoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         newPassword = binding.inputPassword.text.toString(),
         newPasswordConfirm = binding.inputPasswordConfirm.text.toString()
      )
   }

   private fun showMessages() {
      viewModel.showMessages.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun fetchEmployee() {
      viewModel.employeeEntity.observe(viewLifecycleOwner) { employee ->
         binding.inputName.setText(employee.firstname)
         binding.inputSurname.setText(employee.lastname)
         binding.inputAddress.setText(employee.address)
         binding.inputPhoneNumber.setText(employee.phoneNumber)
         binding.autoCompleteJobTitle.setText(employee.job.name, false)
         binding.inputPassword.setText("")
         binding.inputPasswordConfirm.setText("")
      }
   }

   private fun onCallPermissionGranted() {
      val phoneNumber = "tel:+998${binding.inputPhoneNumber.text.toString()}"
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse(phoneNumber)
      requireActivity().startActivity(intent)
   }

   private fun onMessagePermissionGranted() {
      val phoneNumber = "sms:+998${binding.inputPhoneNumber.text.toString()}"
      val sendIntent = Intent(Intent.ACTION_VIEW)
      sendIntent.data = Uri.parse("sms:")
      sendIntent.data = Uri.parse(phoneNumber)
      sendIntent.putExtra("sms_body", "")
      requireActivity().startActivity(sendIntent)
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

   private fun toggleChangesAction(checkState: Boolean) {
      viewModel.setChangeableState(checkState)
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

