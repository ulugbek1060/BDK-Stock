package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.android.bdkstock.R
import com.android.model.utils.observeEvent

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

   abstract val viewModel: BaseViewModel

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.showErrorMessageRes.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }

      viewModel.showErrorMessageEvent.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }

      viewModel.showAuthErrorAndRestart.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), R.string.auth_error, Toast.LENGTH_SHORT).show()
         logout()
      }
   }

   private fun logout() {
      viewModel.logout()
      restartWithSignIn()
   }

   private fun restartWithSignIn() {
      findNavController().navigate(R.id.signInFragment, null, navOptions {
         popUpTo(R.id.activityFragment) {
            inclusive = true
         }
      })
   }
}