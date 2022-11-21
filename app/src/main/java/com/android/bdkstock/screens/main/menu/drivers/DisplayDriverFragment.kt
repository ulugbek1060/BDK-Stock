package com.android.bdkstock.screens.main.menu.drivers

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
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayDriverBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayDriverFragment : BaseFragment(R.layout.fragment_display_driver) {

   override val viewModel by viewModels<DisplayDriverViewModel>()
   private lateinit var binding: FragmentDisplayDriverBinding

   private val requestCallPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotCallRequestPermission
   )

   private val requestMessagePermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotMessageRequestPermission
   )

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayDriverBinding.bind(view)

      observeState()
      observeVehicles()
      observeSelectedVehicle()
      observeDriverFields()
      showSuggestionDialog()
      observeSuccessMessage()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.buttonEdit.setOnClickListener { toggleOnClick() }
      binding.buttonCall.setOnClickListener { callOnClick() }
      binding.buttonMessage.setOnClickListener { messageOnClick() }
   }

   private fun messageOnClick() {
      requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
   }

   private fun callOnClick() {
      requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
   }

   private fun observeDriverFields() {
      //observe driver source
      viewModel.driver.observe(viewLifecycleOwner) { driver ->
         binding.inputName.setText(driver.driverFullName)
         binding.inputPhoneNumber.setText(driver.phoneNumber)
         binding.inputRegNumber.setText(driver.autoRegNumber)
      }
   }

   private fun observeSuccessMessage() {
      viewModel.showSuccessMessage.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun showSuggestionDialog() {
      viewModel.showSuggestionDialog.observe(viewLifecycleOwner) {
         AlertDialog.Builder(requireContext())
            .setTitle(R.string.editing)
            .setMessage(R.string.edit_driver_message)
            .setPositiveButton(R.string.yes) { _, _ ->
               viewModel.enableChangeableState()
            }
            .setNegativeButton(R.string.no) { _, _ ->
               viewModel.disableChangeableState()
            }
            .setCancelable(false)
            .create()
            .show()
      }
   }

   private fun toggleOnClick() {
      viewModel.toggleChangeableState()
   }

   private fun saveOnClick() {
      viewModel.updateDriver(
         fullName = binding.inputName.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         regNumber = binding.inputRegNumber.text.toString()
      )
   }

   private fun observeVehicles() {
      viewModel.vehicles.observe(viewLifecycleOwner) { list ->
         val vehicles = list ?: emptyList()

         val adapter =
            ArrayAdapter(requireContext(), R.layout.auto_complete_item_job_title, vehicles)
         binding.autoCompleteVehicle.setAdapter(adapter)

         binding.autoCompleteVehicle.setOnItemClickListener { _, _, position, _ ->
            viewModel.setVehicle(vehicles[position])
         }
      }
   }

   private fun observeSelectedVehicle() {
      //show changes from ui & current driver
      viewModel.selectedVehicle.observe(viewLifecycleOwner) {
         binding.autoCompleteVehicle.setText(it.name, false)
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         //enable
         binding.inputLayoutName.isEnabled = state.isChangeableEnable
         binding.inputLayoutPhoneNumber.isEnabled = state.isChangeableEnable
         binding.inputLayoutRegNumber.isEnabled = state.isChangeableEnable
         binding.inputLayoutVehicle.isEnabled = state.isChangeableEnable

         //errors
         binding.inputLayoutName.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberErrorMessage(requireContext())
         binding.inputLayoutRegNumber.error = state.getRegNumberErrorMessage(requireContext())
         binding.inputLayoutVehicle.error = state.getVehicleErrorMessage(requireContext())

         //colors
//         binding.containerSaveButton.setBackgroundColor(state.getButtonSaveColor(requireContext()))
         binding.buttonEdit.setTextColor(state.getToggleButtonColor(requireContext()))

         //text
         binding.buttonEdit.setText(state.getToggleButtonText(requireContext()))

         //visibility
         binding.buttonSave.isVisible = state.isChangeableEnable && !state.isInProgress
         binding.lottiProgress.isVisible = state.isInProgress
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

   // - call permission

   private fun onGotCallRequestPermission(granted: Boolean) {
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

   private fun onCallPermissionGranted() {
      val phoneNumber = "tel:+998${binding.inputPhoneNumber.text.toString()}"
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse(phoneNumber)
      requireActivity().startActivity(intent)
   }

   // - message permission

   private fun onGotMessageRequestPermission(granted: Boolean) {
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

   private fun onMessagePermissionGranted() {
      val phoneNumber = "sms:+998${binding.inputPhoneNumber.text.toString()}"
      val sendIntent = Intent(Intent.ACTION_VIEW)
      sendIntent.data = Uri.parse("sms:")
      sendIntent.data = Uri.parse(phoneNumber)
      sendIntent.putExtra("sms_body", "")
      requireActivity().startActivity(sendIntent)
   }
}