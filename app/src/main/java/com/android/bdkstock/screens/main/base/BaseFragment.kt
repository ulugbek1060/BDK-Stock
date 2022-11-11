package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
         anim {
            enter = R.anim.enter
            exit = R.anim.exit
            popEnter = R.anim.pop_enter
            popExit = R.anim.pop_exit
         }
         popUpTo(R.id.activityFragment) {
            inclusive = true
         }
      })
   }

   fun Fragment.navigate(id: Int) {
      findNavController().navigate(id, null, navOptions {
         anim {
            enter = R.anim.enter
            exit = R.anim.exit
            popEnter = R.anim.pop_enter
            popExit = R.anim.pop_exit
         }
      })
   }

   fun Fragment.findTopNavController(): NavController {
      val topLevelHost =
         requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_main_container) as NavHostFragment?
      return topLevelHost?.navController ?: findNavController()
   }
}