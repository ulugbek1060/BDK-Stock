package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.android.bdkstock.R
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.*

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
         logout()
      }
   }

   /**
    * Log out without colling (api/user/logout)  if token is unusable
    */
   private fun logout() {
      viewModel.logout()
      findTopNavController().navigate(R.id.signInFragment, null, navOptions {
         anim {
            enter = R.anim.enter
            exit = R.anim.exit
            popExit = R.anim.pop_enter
            popExit = R.anim.pop_exit
         }
         popUpTo(R.id.actionsFragment) {
            inclusive = true
         }
      })
   }
}