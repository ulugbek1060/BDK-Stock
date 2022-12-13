package com.android.bdkstock.screens.main.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.android.bdkstock.R
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent

abstract class BaseFragment<ViewModel : BaseViewModel, Binding : ViewBinding> : Fragment() {

   protected abstract val viewModel: BaseViewModel
   private var _binding: Binding? = null
   protected val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = getViewBinding()
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.showErrorMessageRes.observeEvent(viewLifecycleOwner) {
         findTopNavController()
            .navigate(R.id.globalErrorFragment, bundleOf(ERROR_MESSAGE_KEY to it))
      }

      viewModel.showErrorMessageEvent.observeEvent(viewLifecycleOwner) {
         findTopNavController()
            .navigate(R.id.globalErrorFragment, bundleOf(ERROR_MESSAGE_KEY to it))
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

   abstract fun getViewBinding(): Binding

   override fun onDestroyView() {
      _binding = null
      super.onDestroyView()
   }

   private companion object {
      const val ERROR_MESSAGE_KEY = "error_message"
   }
}