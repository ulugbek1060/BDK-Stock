package com.android.bdkstock.screens.main.menu.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProfileBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
   BaseFragment<ProfileViewModel, FragmentProfileBinding>() {

   override val viewModel by viewModels<ProfileViewModel>()
   override fun getViewBinding() = FragmentProfileBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.accountInfo.observe(viewLifecycleOwner) {
         binding.inputName.setText(it.firstname)
         binding.inputSurname.setText(it.lastname)
         binding.inputPhoneNumber.setText(it.phoneNumber)
         binding.inputAddress.setText(it.address)

         //button save visible only user is [Admin]
         binding.buttonPermissions.isVisible = it.job?.name == "Admin"
      }

      binding.buttonPermissions.setOnClickListener {
         findNavController().navigate(R.id.profile_to_userPermissions)
      }

      binding.buttonLogout.setOnClickListener { logoutOnClick() }
   }

   private fun logoutOnClick() {
      AlertDialog.Builder(requireContext())
         .setTitle(getString(R.string.logout))
         .setMessage(getString(R.string.logout_message))
         .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.logoutManually() }
         .setNegativeButton(getString(R.string.no)) { _, _ -> }
         .show()
   }
}