package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProfileBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.navigate
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
      }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)

   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_settings, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         return menuItem.onNavDestinationSelected(findTopNavController())
      }
   }
}