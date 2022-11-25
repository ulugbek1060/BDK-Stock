package com.android.bdkstock.screens.main.menu.clients

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentClientsBinding
import com.android.bdkstock.databinding.FragmentDisplayClientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayClientsFragment :
   BaseFragment<DisplayClientsViewModel, FragmentDisplayClientsBinding>() {


   // TODO: need to initialize edit button with menu

   override val viewModel by viewModels<DisplayClientsViewModel>()
   override fun getViewBinding() = FragmentDisplayClientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeClientFields()
      observeSuggestionDialog()
      observeSuccessMessage()

      binding.buttonSave.setOnClickListener { saveOnClick() }
//      binding.buttonEdit.setOnClickListener { toggleOnClick() }
      binding.buttonCall.setOnClickListener { callOnClick() }
      binding.buttonMessage.setOnClickListener { messageOnClick() }

   }

   private fun messageOnClick() {
      requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
   }

   private fun callOnClick() {
      requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
   }

   private fun toggleOnClick() {
      viewModel.toggleChangeableState()
   }

   private fun saveOnClick() {
      viewModel.updateClient(
         fullName = binding.inputName.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         address = binding.inputAddress.text.toString()
      )
   }

   private fun observeSuccessMessage() {
      viewModel.showSuccessMessage.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun observeSuggestionDialog() {
      viewModel.showSuggestionDialog.observeEvent(viewLifecycleOwner) {
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

   private fun observeClientFields() {
      viewModel.clientEntity.observe(viewLifecycleOwner) {
         binding.inputName.setText(it.fullName)
         binding.inputPhoneNumber.setText(it.phoneNumber)
         binding.inputAddress.setText(it.address)
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         // enable
         binding.inputLayoutName.isEnabled = state.isChangeableEnable
         binding.inputLayoutPhoneNumber.isEnabled = state.isChangeableEnable
         binding.inputLayoutAddress.isEnabled = state.isChangeableEnable

         // errors
         binding.inputLayoutName.error = state.getFullNameErrorMessage(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberErrorMessage(requireContext())
         binding.inputLayoutAddress.error = state.getAddressErrorMessage(requireContext())

         // color
//         binding.buttonEdit.setTextColor(state.getToggleButtonColor(requireContext()))

         // text
//         binding.buttonEdit.setText(state.getToggleButtonText(requireContext()))

         //visibility
         binding.buttonSave.isVisible = state.isChangeableEnable && !state.isInProgress
         binding.lottiProgress.isVisible = state.isInProgress
      }
   }

   // -- call permission launch ------------------------------


   private val requestCallPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotCallRequestPermission
   )

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

   // -- message permission launch ------------------------------


   private val requestMessagePermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission(),
      ::onGotMessageRequestPermission
   )

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


   // if permission is denied ----------------------------------------------

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
}