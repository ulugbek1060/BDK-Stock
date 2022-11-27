package com.android.bdkstock.screens.main.menu.employees

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayEmployeeFragment :
   BaseFragment<DisplayEmployeeViewModel, FragmentDisplayEmployeeBinding>() {

   override val viewModel by viewModels<DisplayEmployeeViewModel>()
   override fun getViewBinding() = FragmentDisplayEmployeeBinding.inflate(layoutInflater)

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

      fetchEmployee()
      setupJobTitle()
      observeState()

      observeClearPassword()
      showMessages()

      observeChangesDialogEvent()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.buttonDelete.setOnClickListener { }

      binding.buttonCall.setOnClickListener {
         requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
      }

      binding.buttonMessage.setOnClickListener {
         requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
      }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_edit, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.edit) viewModel.toggleChangeableState()
         return false
      }
   }

   private fun observeChangesDialogEvent() {
      viewModel.showSuggestionDialog.observeEvent(viewLifecycleOwner) {
         AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit)
            .setMessage(R.string.edit_user_details)
            .setNegativeButton(R.string.no) { _, _ -> viewModel.disableChangeableState() }
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
      viewModel.jobsEntity.observe(viewLifecycleOwner) { list ->
         val adapter = ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, list)
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
         binding.inputLayoutName.isEnabled = state.isChangeableEnable
         binding.inputLayoutSurname.isEnabled = state.isChangeableEnable
         binding.inputLayoutAddress.isEnabled = state.isChangeableEnable
         binding.inputLayoutPhoneNumber.isEnabled = state.isChangeableEnable
         binding.inputLayoutPassword.isEnabled = state.isChangeableEnable
         binding.inputLayoutJobTitle.isEnabled = state.isChangeableEnable
         binding.inputLayoutPasswordConfirm.isEnabled = state.isChangeableEnable

         binding.buttonSave.isEnabled = state.isChangeableEnable
         binding.buttonDelete.isEnabled = state.isChangeableEnable

         // error messages
         binding.inputLayoutName.error = state.getNameError(requireContext())
         binding.inputLayoutSurname.error = state.getSurnameError(requireContext())
         binding.inputLayoutAddress.error = state.getAddressError(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberError(requireContext())
         binding.inputLayoutPassword.error = state.getPasswordError(requireContext())
         binding.inputLayoutPasswordConfirm.error = state.getPasswordError(requireContext())

         // visibility
         binding.buttonSave.isVisible = state.isChangeableEnable && !state.isProgressActive
         binding.buttonDelete.isVisible = state.isChangeableEnable && !state.isProgressActive
         binding.lottiProgress.isVisible = state.isProgressActive
      }
   }
}


