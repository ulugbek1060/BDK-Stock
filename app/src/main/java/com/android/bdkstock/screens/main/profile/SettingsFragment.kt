package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSettingsBinding
import com.android.bdkstock.screens.main.base.BaseFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

   private lateinit var binding: FragmentSettingsBinding
   override val viewModel by viewModels<SettingsViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSettingsBinding.bind(view)


   }
}