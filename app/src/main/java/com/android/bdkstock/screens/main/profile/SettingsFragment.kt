package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSettingsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

   private lateinit var binding: FragmentSettingsBinding
   override val viewModel by viewModels<SettingsViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSettingsBinding.bind(view)

      observeLogoutActions()

      binding.buttonLogout.setOnClickListener { viewModel.logoutManually() }
   }

   private fun observeLogoutActions() {
      viewModel.doLogoutActions.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
         viewModel.restart()
      }
   }
}