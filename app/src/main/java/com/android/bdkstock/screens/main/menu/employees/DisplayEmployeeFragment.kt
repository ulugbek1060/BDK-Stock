package com.android.bdkstock.screens.main.menu.employees

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
import com.android.bdkstock.databinding.FragmentDisplayEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayEmployeeFragment :
   BaseFragment<DisplayEmployeeViewModel, FragmentDisplayEmployeeBinding>() {

   override val viewModel by viewModels<DisplayEmployeeViewModel>()
   override fun getViewBinding() = FragmentDisplayEmployeeBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      observeEmployee()
      observeNavigateToEdit()

      getFragmentResult()


      binding.buttonCall.setOnClickListener {
         requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
      }

      binding.buttonMessage.setOnClickListener {
         requestMessagePermissionLauncher.launch(Manifest.permission.SEND_SMS)
      }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }


   private fun observeNavigateToEdit() {
      viewModel.navigateToEdit.observeEvent(viewLifecycleOwner) {
         val args = DisplayEmployeeFragmentDirections
            .displayEmployeeToEditEmployee(it)
         findTopNavController().navigate(args)
      }
   }


   private fun observeEmployee() {
      viewModel.employee.observe(viewLifecycleOwner) { employee ->
         binding.tvFullName.text = "${employee.firstname} ${employee.lastname}"
         binding.tvMobile.text = "+998${employee.phoneNumber}"
         binding.tvAddress.text = employee.address
         binding.tvJob.text = employee.job.name
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

   private fun getFragmentResult() {
      setFragmentResultListener(EMPLOYEE_ENTITY_KEY) { _, bundle ->
         val clientEntity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(EMPLOYEE_ENTITY_BUNDLE_KEY, EmployeeEntity::class.java)
         } else {
            bundle.getSerializable(EMPLOYEE_ENTITY_BUNDLE_KEY) as EmployeeEntity
         }
         viewModel.setUpdatedEntity(clientEntity)
      }
   }


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

   private fun onCallPermissionGranted() {
      val phoneNumber = "tel:+998${binding.tvMobile.text}"
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse(phoneNumber)
      requireActivity().startActivity(intent)
   }

   private fun onMessagePermissionGranted() {
      val phoneNumber = "sms:+998${binding.tvMobile.text}"
      val sendIntent = Intent(Intent.ACTION_VIEW)
      sendIntent.data = Uri.parse("sms:")
      sendIntent.data = Uri.parse(phoneNumber)
      sendIntent.putExtra("sms_body", "")
      requireActivity().startActivity(sendIntent)
   }


   private companion object {
      const val EMPLOYEE_ENTITY_KEY = "client_entity"
      const val EMPLOYEE_ENTITY_BUNDLE_KEY = "client_entity_bundle"
   }
}


