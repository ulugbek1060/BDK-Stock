package com.android.bdkstock.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentActionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActionsFragment : Fragment() {

   private var _binding: FragmentActionsBinding? = null
   private val binding: FragmentActionsBinding get() = _binding!!
   private lateinit var navController: NavController

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = FragmentActionsBinding
      .inflate(inflater, container, false)
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      val navHost = childFragmentManager
         .findFragmentById(R.id.fragment_actions_container) as NavHostFragment

      navController = navHost.navController

      NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
   }


}