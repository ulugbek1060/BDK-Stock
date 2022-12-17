package com.android.bdkstock.screens.main.menu.drivers

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
import com.android.bdkstock.databinding.FragmentDisplayDriverBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.menu.clients.DisplayClientsFragmentDirections
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayDriverFragment :
   BaseFragment<DisplayDriverViewModel, FragmentDisplayDriverBinding>() {

   override val viewModel by viewModels<DisplayDriverViewModel>()
   override fun getViewBinding() = FragmentDisplayDriverBinding.inflate(layoutInflater)

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

      observeDriver()
      observeNavigateToEdit()

      getFragmentResult()


      binding.buttonCall.setOnClickListener { callOnClick() }
      binding.buttonMessage.setOnClickListener { messageOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun observeDriver() {
      viewModel.driver.observe(viewLifecycleOwner){ driver ->
         with(binding){
            tvFullName.text = driver.driverFullName
            tvMobile.text = "+998${driver.phoneNumber}"
            tvAuto.text = driver.vehicle.name
            tvRegNumber.text = driver.autoRegNumber
         }
      }
   }

   private fun getFragmentResult() {
      setFragmentResultListener(DRIVER_ENTITY_KEY){ _, bundle ->
         val driverEntity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(DRIVER_ENTITY_BUNDLE_KEY, DriverEntity::class.java)
         } else {
            bundle.getSerializable(DRIVER_ENTITY_BUNDLE_KEY) as DriverEntity
         }
         viewModel.setUpdatedEntity(driverEntity)
      }
   }

   private fun observeNavigateToEdit() {
      viewModel.navigateToEdit.observeEvent(viewLifecycleOwner) {
         val args = DisplayDriverFragmentDirections
            .displayDriverToEditDriver(it)
         findTopNavController().navigate(args)
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
      val phoneNumber = "tel:+998${binding.tvMobile.text}"
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
      val phoneNumber = "sms:+998${binding.tvMobile.text}"
      val sendIntent = Intent(Intent.ACTION_VIEW)
      sendIntent.data = Uri.parse("sms:")
      sendIntent.data = Uri.parse(phoneNumber)
      sendIntent.putExtra("sms_body", "")
      requireActivity().startActivity(sendIntent)
   }


   private companion object{
      const val DRIVER_ENTITY_KEY = "driver_entity"
      const val DRIVER_ENTITY_BUNDLE_KEY = "driver_entity_bundle"
   }
}