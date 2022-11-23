package com.android.bdkstock.screens.main.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
   ): View? {
      _binding = getViewBinding()
      return _binding!!.root
   }

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

   abstract fun getViewBinding(): Binding

   fun String.formatDate(context: Context): String = try {
      //2022-11-20T10:19:25.000000Z
      val timeArray = this.split('-')
      val year = timeArray[0]
      val month = timeArray[1]
      val day = timeArray[2].substring(0, 1)
      val txtMonth = when (month) {
         "01", "1" -> {
            context.getString(R.string.m_01)
         }
         "02", "2" -> {
            context.getString(R.string.m_02)
         }
         "03", "3" -> {
            context.getString(R.string.m_03)
         }
         "04", "4" -> {
            context.getString(R.string.m_04)
         }
         "05", "5" -> {
            context.getString(R.string.m_05)
         }
         "06", "6" -> {
            context.getString(R.string.m_06)
         }
         "07", "7" -> {
            context.getString(R.string.m_07)
         }
         "08", "8" -> {
            context.getString(R.string.m_08)
         }
         "09", "9" -> {
            context.getString(R.string.m_09)
         }
         "10" -> {
            context.getString(R.string.m_10)
         }
         "11" -> {
            context.getString(R.string.m_11)
         }
         "12" -> {
            context.getString(R.string.m_12)
         }
         else -> {
            ""
         }
      }
      "$day $txtMonth $year"
   } catch (e: Exception) {
      this
   }

   override fun onDestroyView() {
      _binding = null
      super.onDestroyView()
   }
}