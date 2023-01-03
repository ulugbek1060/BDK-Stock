package com.android.bdkstock.screens.main.menu.clients

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayClientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayClientsFragment :
   BaseFragment<DisplayClientsViewModel, FragmentDisplayClientsBinding>() {

   override val viewModel by viewModels<DisplayClientsViewModel>()
   override fun getViewBinding() = FragmentDisplayClientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeClient()
      observeNavigateToEdit()

      getFragmentResult()

      binding.buttonCall.setOnClickListener { callOnClick() }
      binding.buttonMessage.setOnClickListener { messageOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun getFragmentResult() {
      setFragmentResultListener(CLIENT_ENTITY_KEY) { _, bundle ->
         val clientEntity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(CLIENT_ENTITY_BUNDLE_KEY, ClientEntity::class.java)
         } else {
            bundle.getSerializable(CLIENT_ENTITY_BUNDLE_KEY) as ClientEntity
         }
         viewModel.setUpdatedEntity(clientEntity)
      }
   }

   private fun observeNavigateToEdit() {
      viewModel.navigateToEdit.observeEvent(viewLifecycleOwner) {
         val args = DisplayClientsFragmentDirections.displayClientsToEditClient(it)
         findTopNavController().navigate(args)
      }
   }

   @SuppressLint("SetTextI18n")
   private fun observeClient() {
      viewModel.client.observe(viewLifecycleOwner) { client ->
         with(binding) {
            tvFullName.text = client.fullName
            inputPhoneNumber.setText(client.phoneNumber)
            inputAddress.setText(client.address)
         }
      }
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_edit, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.edit) viewModel.navigateToEdit()
         return false
      }
   }

   private fun messageOnClick() {
      requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
   }

   private fun callOnClick() {
      requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
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
      val phoneNumber = "tel:+998${binding.inputPhoneNumber.text}"
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
      val phoneNumber = "sms:+998${binding.inputPhoneNumber.text}"
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


   private companion object {
      const val CLIENT_ENTITY_KEY = "client_entity"
      const val CLIENT_ENTITY_BUNDLE_KEY = "client_entity_bundle"
   }
}