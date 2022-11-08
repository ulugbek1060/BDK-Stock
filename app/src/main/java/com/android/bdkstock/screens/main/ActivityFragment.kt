package com.android.bdkstock.screens.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityFragment : Fragment(R.layout.fragment_activity) {

   private lateinit var binding: FragmentActivityBinding
   private lateinit var navController: NavController

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentActivityBinding.bind(view)

      val navHost =
         childFragmentManager.findFragmentById(R.id.fragment_activities_container) as NavHostFragment
      navController = navHost.navController

      NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
   }
}